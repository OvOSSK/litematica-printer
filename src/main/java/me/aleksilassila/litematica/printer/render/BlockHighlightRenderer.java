package me.aleksilassila.litematica.printer.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fi.dy.masa.malilib.interfaces.IRenderer;
import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.enums.HighlightStyleType;
import me.aleksilassila.litematica.printer.enums.HighlightType;
import me.aleksilassila.litematica.printer.runtime.RuntimeAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;

/** Geometry and styles adapted from BiliXWhite/litematica-printer@6cef6d31 (AGPL-3.0).
 * The renderer reads bounded snapshots only; it never visits the world or advances an iterator.
 */
public final class BlockHighlightRenderer implements IRenderer {
    @Override
    public void onRenderWorldLast(PoseStack pose, org.joml.Matrix4f projection) {
        Vec3 eye = net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        if (!Configs.Highlight.ENABLED.getBooleanValue()) return;
        var runtime = RuntimeAccess.currentOrNull();
        if (runtime == null || runtime.client().level == null || runtime.operationHighlights() == null) return;
        var trace = runtime.operationHighlights();
        long duration = Configs.Highlight.FADE_DURATION.getIntegerValue() * 100L;
        long now = trace.now();
        var style = (HighlightStyleType) Configs.Highlight.STYLE.getOptionListValue();
        List<HighlightEntry> entries = new ArrayList<>();
        var previews = trace.previews();
        var previewPositions = new HashSet<BlockPos>();
        float previewFill = 0.13f + 0.04f * (float) Math.sin(now % 1800L * (Math.PI * 2.0 / 1800.0));
        for (var preview : previews) {
            if (previewPositions.add(preview.pos())) entries.add(entry(preview.pos(), Configs.Highlight.NEXT.getIntegerValue(), 1f,
                    HighlightStyleType.BOTH, true, eye));
        }
        for (var operation : trace.snapshot(duration)) {
            if (previewPositions.contains(operation.pos())) continue;
            float fade = 1f - net.minecraft.util.Mth.clamp((float)(now - operation.time()) / duration, 0f, 1f);
            entries.add(entry(operation.pos(), color(operation.type()), fade, style, false, eye));
        }
        if (!entries.isEmpty()) this.draw(entries, eye, previewFill, pose);
    }

    private static int color(HighlightType type) {
        return switch (type) {
            case PLACE -> Configs.Highlight.PLACE.getIntegerValue();
            case ADJUST -> Configs.Highlight.ADJUST.getIntegerValue();
            case BREAK -> Configs.Highlight.BREAK.getIntegerValue();
            case FAILED -> Configs.Highlight.FAILED.getIntegerValue();
        };
    }

    private static HighlightEntry entry(BlockPos pos, int argb, float fade, HighlightStyleType style, boolean preview, Vec3 camera) {
        return new HighlightEntry(pos, ((argb >>> 24) & 255) / 255f * fade,
                Vec3.atCenterOf(pos).distanceToSqr(camera), (argb >>> 16) & 255, (argb >>> 8) & 255, argb & 255, style, preview);
    }

    private void draw(List<HighlightEntry> entries, Vec3 cameraPos, float previewFill, PoseStack pose) {
        boolean through = Configs.Highlight.THROUGH_WALLS.getBooleanValue();
        entries.sort(Comparator.comparingDouble(e -> -e.distSq));
        var matrices = RenderSystem.getModelViewStack();
        matrices.pushPose();
        matrices.mulPoseMatrix(pose.last().pose());
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        if (through) RenderSystem.disableDepthTest();
        else RenderSystem.enableDepthTest();
        try {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            var buf = Tesselator.getInstance().getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (var e : entries) if (e.style != HighlightStyleType.OUTLINE)
                addFilledBoxModern(buf, e.pos, e.r, e.g, e.b, (int)(e.alpha * 255 * (e.preview ? previewFill : 1f)), cameraPos);
            BufferUploader.drawWithShader(buf.end());
            buf.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
            for (var e : entries) if (e.style != HighlightStyleType.FILLED)
                addOutlineBoxLines(buf, e.pos, e.r, e.g, e.b, (int)(e.alpha * 255), cameraPos);
            BufferUploader.drawWithShader(buf.end());
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            matrices.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private void addFilledBoxModern(BufferBuilder buf, BlockPos pos,
                                    int r, int g, int b, int a, Vec3 cameraPos) {
        float x1 = (float) (pos.getX() - cameraPos.x - 0.001);
        float y1 = (float) (pos.getY() - cameraPos.y - 0.001 );
        float z1 = (float) (pos.getZ() - cameraPos.z - 0.001 );
        float x2 = (float) (pos.getX() - cameraPos.x + 1 + 0.001);
        float y2 = (float) (pos.getY() - cameraPos.y + 1 + 0.001);
        float z2 = (float) (pos.getZ() - cameraPos.z + 1 + 0.001);

        quadModern(buf, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, r, g, b, a);
        quadModern(buf, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, r, g, b, a);
        quadModern(buf, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, r, g, b, a);
        quadModern(buf, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, r, g, b, a);
        quadModern(buf, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, r, g, b, a);
        quadModern(buf, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, r, g, b, a);
    }

    private void quadModern(BufferBuilder buf, float x1, float y1, float z1,
                            float x2, float y2, float z2, float x3, float y3, float z3,
                            float x4, float y4, float z4, int r, int g, int b, int a) {
        buf.vertex(x1, y1, z1).color(r, g, b, a).endVertex();
        buf.vertex(x2, y2, z2).color(r, g, b, a).endVertex();
        buf.vertex(x3, y3, z3).color(r, g, b, a).endVertex();
        buf.vertex(x4, y4, z4).color(r, g, b, a).endVertex();
    }

    private void addOutlineBoxLines(BufferBuilder buf, BlockPos pos,
                                    int r, int g, int b, int a, Vec3 cameraPos) {
        float x1 = (float) (pos.getX() - cameraPos.x );
        float y1 = (float) (pos.getY() - cameraPos.y );
        float z1 = (float) (pos.getZ() - cameraPos.z );
        float x2 = (float) (pos.getX() - cameraPos.x + 1 );
        float y2 = (float) (pos.getY() - cameraPos.y + 1 );
        float z2 = (float) (pos.getZ() - cameraPos.z + 1 );

        line(buf, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buf, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buf, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buf, x1, y1, z2, x1, y1, z1, r, g, b, a);
        line(buf, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buf, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buf, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buf, x1, y2, z2, x1, y2, z1, r, g, b, a);
        line(buf, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buf, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buf, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buf, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private void line(BufferBuilder buf, float x1, float y1, float z1,
                      float x2, float y2, float z2, int r, int g, int b, int a) {
        buf.vertex(x1, y1, z1).color(r, g, b, a).endVertex();
        buf.vertex(x2, y2, z2).color(r, g, b, a).endVertex();
    }
    private record HighlightEntry(BlockPos pos, float alpha, double distSq, int r, int g, int b, HighlightStyleType style, boolean preview) { }
}
