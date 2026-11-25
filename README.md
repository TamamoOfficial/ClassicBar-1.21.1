# Classic Bar — NeoForge 1.21.1

把原版 HUD 的图标状态（心形生命、鸡腿饥饿、护甲、氧气等）换成条形显示。
[Classic Bar](https://github.com/Tfarcenim/ClassicBar) 的 1.21.1 / NeoForge 移植版。

> Replaces the vanilla HUD icon rows with icon bars. NeoForge 1.21.1 port of Classic Bar by Tfarecnim.

## 状态条

生命（随最大生命值缩放，中毒/凋零变色）、护甲、护甲韧性、伤害吸收、氧气、
饥饿（含饱和度、手持食物预览、饥饿消耗）、坐骑生命、ParCool 体力（可选）。

数值、图标、左右位置、颜色均可配置。

## 安装

Minecraft 1.21.1 + NeoForge 21.1.x，jar 丢进 `mods/` 即可，无强制前置。
HUD 纯客户端；服务端也装的话饱和度与饥饿消耗会同步得更实时。

## 配置

`config/classicbar-client.toml`，改完即时生效。可调整条形顺序、是否显示图标、
是否撑满整宽、各条颜色等。

## 构建

需要 JDK 21。

```bash
./gradlew build
# 产物：build/libs/ClassicBar-3.0.0-1.21.1.jar
```

## 致谢

原版设计归 [Tfarecnim](https://github.com/Tfarcenim/ClassicBar)，
代码基础来自 [Waterpicker 的 1.20.1 分支](https://github.com/Waterpicker/ClassicBar/tree/1.20.1)，
另参考 [Classic-Bar-Legacy](https://github.com/CanoeStudioOfficial/Classic-Bar-Legacy)。
