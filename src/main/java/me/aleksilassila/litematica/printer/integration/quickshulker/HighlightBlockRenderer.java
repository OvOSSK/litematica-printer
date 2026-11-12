package me.aleksilassila.litematica.printer.integration.quickshulker;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import me.aleksilassila.litematica.printer.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;


import java.util.*;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;



import org.joml.Matrix4f;

@SuppressWarnings("deprecation")
public class HighlightBlockRenderer implements IRenderer {
    public static HighlightBlockRenderer instance = new HighlightBlockRenderer();
    private static final Object HIGHLIGHT_LOCK = new Object();
    public static Map<String, HighlightTheProject> highlightTheProjectMap = new HashMap<>();
    public static String threadName = Reference.MOD_ID + "-render";
    public static boolean shaderIng = false;
    private static boolean renderFailureLogged = false;
    public static List<String> clearList = new LinkedList<>();
    public static Map<String, Set<BlockPos>> setMap = new HashMap<>();

    public static void createHighlightBlockList(String id, ConfigColor color4f) {
        synchronized (HIGHLIGHT_LOCK) {
            if (highlightTheProjectMap.get(id) == null) {
                highlightTheProjectMap.put(id, new HighlightTheProject(color4f, new LinkedHashSet<>()));
            }
        }
    }

    public static Set<BlockPos> getHighlightBlockPosList(String id) {
        synchronized (HIGHLIGHT_LOCK) {
            if (highlightTheProjectMap.get(id) != null) {
                return highlightTheProjectMap.get(id).pos();
            }
        }
        return null;
    }

    public static void clear(String id) {
        synchronized (HIGHLIGHT_LOCK) {
            if (!clearList.contains(id)) clearList.add(id);
        }
    }

    public static void setPos(String id, Set<BlockPos> posSet) {
        synchronized (HIGHLIGHT_LOCK) {
            HighlightTheProject highlightTheProject = highlightTheProjectMap.get(id);
            if (highlightTheProject != null && posSet != null) {
                setMap.put(id, new LinkedHashSet<>(posSet));
            }
        }
    }

    //如果不注册无法渲染，
    public static void init() {
        RenderEventHandler.getInstance().registerWorldLastRenderer(instance);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client1) -> {
            synchronized (HIGHLIGHT_LOCK) {
                for (Map.Entry<String, HighlightTheProject> stringHighlightTheProjectEntry : highlightTheProjectMap.entrySet()) {
                    stringHighlightTheProjectEntry.getValue().pos.clear();
                }
            }
        });
    }

    // @formatter:off
    public void test3(PoseStack matrices, Color4f color4f, Set<BlockPos> posSet){
        for (BlockPos pos : posSet) {
            fi.dy.masa.litematica.render.RenderUtils.renderAreaSides(pos, pos, color4f, matrices, Minecraft.getInstance());
        }

        RenderSystem.enableBlend();
        RenderSystem.disableCull();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();

        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (BlockPos pos : posSet) {
            fi.dy.masa.litematica.render.RenderUtils.renderAreaSidesBatched(pos, pos, color4f, 0.002, buffer, Minecraft.getInstance());
        }
        try {
            if (buffer != null) {
                tesselator.end();
            }
        } catch (Exception e) {
            logRenderFailure(e);
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();


//        fi.dy.masa.litematica.render.fi.dy.masa.litematica.render.RenderUtils.renderAreaSides(pos, pos, color4f, matrices, client);
    }

    @Override
    public void onRenderWorldLast(PoseStack matrices, Matrix4f projMatrix){
        List<HighlightRenderSnapshot> snapshots = new ArrayList<>();
        synchronized (HIGHLIGHT_LOCK) {
            //更改渲染
            setMap.forEach((k, v) -> {
                HighlightTheProject highlightTheProject = highlightTheProjectMap.get(k);
                if (highlightTheProject != null) {
                    highlightTheProject.pos.clear();
                    highlightTheProject.pos.addAll(v);
                }
            });
            setMap.clear();

            for (String string : clearList) {
                HighlightTheProject highlightTheProject = highlightTheProjectMap.get(string);
                if (highlightTheProject != null) {
                    highlightTheProject.pos.clear();
                }
            }
            clearList.clear();

            for (HighlightTheProject value : highlightTheProjectMap.values()) {
                if (!value.pos.isEmpty()) {
                    snapshots.add(new HighlightRenderSnapshot(value.color4f.getColor(), new LinkedHashSet<>(value.pos)));
                }
            }
        }

        shaderIng = true;
        try {
            for (HighlightRenderSnapshot snapshot : snapshots) {
                test3(matrices, snapshot.color, snapshot.pos);
            }
        } catch (RuntimeException exception) {
            logRenderFailure(exception);
        } finally {
            shaderIng = false;
        }
    }

    private static void logRenderFailure(Throwable exception) {
        if (!renderFailureLogged) {
            renderFailureLogged = true;
            Reference.LOGGER.warn("潜影盒高亮渲染失败，已跳过本次渲染", exception);
        }
    }

    // @formatter:on

    public record HighlightTheProject(ConfigColor color4f, Set<BlockPos> pos) {
    }

    private record HighlightRenderSnapshot(Color4f color, Set<BlockPos> pos) {
    }
}
