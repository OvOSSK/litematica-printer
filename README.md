# Litematica Printer Hana — Fabric 1.20.1

由 **OvOSSK** 维护的 [Yur1Ca/litematica-printer（Hana）](https://github.com/Yur1Ca/litematica-printer) 社区移植版。基于 Hana old 分支的 1.20.1 代码，手动回移虚空仓库联动。本分支只构建 Minecraft 1.20.1。

## 安装

从 Releases 下载不含 sources 的 JAR，仅安装于 Fabric 1.20.1 客户端。移除其他同 ID 打印机 JAR，避免重复加载。

需要 Java 17+、Fabric Loader 0.16.14+、1.20.1 Fabric API、MaLiLib 0.16.3、Litematica 0.15.4。仓库联动需要服务端安装 [Neko 主模组移植版](https://github.com/OvOSSK/Neko-Carpet-Addition)并启用相应规则。

投影菜单 → 打印机设置 → 特殊功能，可启用仓库补货、直接放置、余料回仓及材料列表统计。默认工作切换键为 Caps Lock。余料回仓保护当前手持材料：停止打印后切换手持槽位，可返还该槽位的仓库余料。

## 构建与验证

Java 17，运行 `./gradlew build`（Windows：`gradlew.bat build`）。Gradle 8.5、Loom 1.5.8；所有源代码、资源、测试和构建脚本包含于仓库。可选兼容模组构建依赖见 build.gradle。

50 项仓库材料与放置测试通过。实机验证了配置界面、材料库存查询、生存补货打印、余料回仓及仓库直接放置，四方块测试扣料正确。见 [验证记录](VALIDATION-2026-11-13.txt)。复杂方块、大型投影与远程多人环境尚未全面验证。

## 来源、修改与许可

保留 aleksilassila、zhaixianyu、BlinkWhite、bunnyi116 等上游贡献者署名。项目继续按 [GNU AGPL v3](LICENSE.md) 分发，移植修改采用相同许可。

修改包括 Java 17 独立构建、Minecraft/Fabric 旧 API 适配、网络编解码、Mixin 兼容和仓库联动回移。修改日期：2026-11-13。每个 Release 同时提供与二进制对应的完整源码及构建脚本。

Neko 协议兼容代码的 MIT 版权和许可保存在 src/main/resources/licenses/Neko-Carpet-Addition-MIT.txt，随 JAR 分发。详见 [移植说明](PORT-NOTICE.txt)。OvOSSK 仅署名移植维护贡献，本版本不是上游官方发布。
