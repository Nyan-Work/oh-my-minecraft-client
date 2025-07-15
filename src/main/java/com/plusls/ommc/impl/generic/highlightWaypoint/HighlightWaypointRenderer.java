package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.mojang.blaze3d.vertex.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.MagicLib;
import top.hendrixshen.magiclib.api.compat.minecraft.client.gui.FontCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.compat.mojang.blaze3d.vertex.VertexFormatCompat;
import top.hendrixshen.magiclib.api.event.minecraft.render.RenderLevelListener;
import top.hendrixshen.magiclib.api.render.context.RenderContext;
import top.hendrixshen.magiclib.impl.render.context.RenderGlobal;
import top.hendrixshen.magiclib.util.minecraft.PositionUtil;
import top.hendrixshen.magiclib.util.minecraft.render.RenderUtil;

//#if MC > 12006
//$$ import top.hendrixshen.magiclib.api.compat.mojang.blaze3d.vertex.VertexFormatCompat;
//#endif

//#if MC < 11900
//$$ import net.minecraft.client.Option;
//#endif

//#if MC > 11605
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.GameRenderer;
//#else
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//$$ import org.lwjgl.opengl.GL11;
//$$ import java.util.Objects;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//$$ import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//#endif

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HighlightWaypointRenderer implements RenderLevelListener {
    @Getter
    private static final HighlightWaypointRenderer instance = new HighlightWaypointRenderer();
    private static final ResourceLocation BEAM_LOCATION = ResourceLocationCompat.withDefaultNamespace("textures/entity/beacon_beam.png");

    public TextureAtlasSprite targetIdSprite;
    protected long lastBeamTime = 0;

    public static void init() {
        MagicLib.getInstance().getEventManager().register(RenderLevelListener.class, HighlightWaypointRenderer.instance);
    }

    @Override
    public void preRenderLevel(Level level, RenderContext context, float partialTicks) {
        // NO-OP
    }

    @Override
    public void postRenderLevel(Level level, RenderContext context, float partialTicks) {
        BlockPos waypointPos = HighlightWaypointHandler.getInstance().getHighlightPos();

        if (waypointPos == null) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getEntity().getEyePosition(partialTicks);
        //#if MC > 11802
        double maxDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
        //#else
        //$$ double maxDistance = Option.RENDER_DISTANCE.get(Minecraft.getInstance().options) * 16;
        //#endif
        Vec3 target = PositionUtil.centerOf(waypointPos);
        double distance = target.distanceTo(cameraPos);
        double renderDistance = distance;

        if (distance > maxDistance) {
            Vec3 direction = target.subtract(cameraPos);
            target = cameraPos.add(direction.normalize().multiply(maxDistance, maxDistance, maxDistance));
            renderDistance = maxDistance;
        }

        Vec3 vec3 = target.subtract(cameraPos);
        context = RenderContext.of(
                //#if MC > 11502
                new PoseStack()
                //#endif
        );
        context.pushMatrix();
        context.translate(vec3.x(), vec3.y(), vec3.z());
        RenderGlobal.disableDepthTest();

        if (this.lastBeamTime >= System.currentTimeMillis()) {
            context.pushMatrix();
            context.translate(-0.5, -0.5, -0.5);
            // TODO: 1.16+ RenderType hook to support beam seeThrough
            this.renderBeam(level, context, partialTicks);
            context.popMatrix();
        }

        context.pushMatrix();
        //#if MC > 11404
        context.mulPoseMatrix(
                //#if MC > 11902
                new Matrix4f().rotation(camera.rotation())
                //#else
                //$$ new Matrix4f(camera.rotation())
                //#endif
        );
        //#else
        //$$ EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        //$$ GlStateManager.rotatef(-entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
        //$$ GlStateManager.rotatef(entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
        //#endif

        float scale = (float) ((renderDistance > 8 ? renderDistance - 8 : 0) * 0.2 + 1) * 0.0265F;
        context.scale(RenderUtil.getSizeScalingXSign() * scale, -scale, -scale);

        context.pushMatrix();
        context.translate(0.0, 5.0, 0.0);
        this.renderText(context, String.format("x:%d, y:%d, z:%d (%dm)",
                waypointPos.getX(), waypointPos.getY(), waypointPos.getZ(), (int) distance));
        context.popMatrix();

        RenderGlobal.disableDepthTest();
        this.renderIcon(context);
        RenderGlobal.enableDepthTest();
        context.popMatrix();
        context.popMatrix();
    }

    private void renderBeam(@NotNull Level level, @NotNull RenderContext context, float partialTicks) {
        //#if MC > 11404
        MultiBufferSource.BufferSource bufferBuilder = RenderUtil.getBufferSource();
        //#else
        //$$ Minecraft.getInstance().getTextureManager().bind(HighlightWaypointRenderer.BEAM_LOCATION);
        //#endif

        BeaconRenderer.renderBeaconBeam(
                //#if MC > 11502
                context.getMatrixStack().getPoseStack(),
                //#elseif MC > 11404
                //$$ new PoseStack(),
                //#else
                //$$ 0,
                //$$ 0,
                //$$ 0,
                //#endif
                //#if MC > 11404
                bufferBuilder,
                HighlightWaypointRenderer.BEAM_LOCATION,
                //#endif
                partialTicks,
                1.0F,
                level.getGameTime(),
                -128,
                256,
                //#if MC > 12006
                //$$ 0xFF0000,
                //#else
                new float[]{1.0f, 0.0f, 0.0f},
                //#endif
                0.2F,
                0.25F
        );
        //#if MC > 11404
        bufferBuilder.endBatch();
        //#endif

        RenderGlobal.enableBlend();

        //#if MC > 11605
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //#endif
    }

    private void renderText(@NotNull RenderContext context, String text) {
        FontCompat fontCompat = FontCompat.of(Minecraft.getInstance().font);
        int halfTextWidth = fontCompat.get().width(text) / 2;
        int bgColor = 0x80000000;

        //#if MC < 11700
        //$$ RenderGlobal.disableLighting();
        //#endif

        while (true) {
            //#if MC > 11404
            MultiBufferSource.BufferSource immediate = RenderUtil.getBufferSource();
            //#endif

            fontCompat.drawInBatch(
                    text,
                    (float) -halfTextWidth,
                    0.0F,
                    0xFFFFFF,
                    false,
                    //#if MC > 11404
                    //#if MC > 11502
                    context.getMatrixStack().getPoseStack().last().pose(),
                    //#else
                    //$$ new PoseStack().last().pose(),
                    //#endif
                    immediate,
                    //#endif
                    FontCompat.DisplayMode.SEE_THROUGH,
                    bgColor,
                    0xF000F0
            );

            //#if MC > 11404
            immediate.endBatch();
            //#endif

            if (bgColor == 0) {
                break;
            } else {
                bgColor = 0;
            }
        }

        //#if MC < 11600
        //$$ RenderGlobal.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //#endif

        //#if MC < 11904
        RenderGlobal.enableDepthTest();
        //#endif
    }

    private void renderIcon(@NotNull RenderContext context) {
        TextureAtlasSprite icon = HighlightWaypointResourceLoader.targetIdSprite;
        RenderGlobal.enableBlend();

        //#if MC > 11605
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        //#elseif MC > 11404
        //$$ RenderSystem.bindTexture(Objects.requireNonNull(Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getId());
        //#else
        //$$ GlStateManager.bindTexture(Objects.requireNonNull(Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getId());
        //#endif

        //#if MC < 11904
        //$$ RenderGlobal.enableTexture();
        //#endif

        Tesselator tesselator = Tesselator.getInstance();
        //#if MC > 12006
        //$$ BufferBuilder bufferBuilder = tesselator.begin(VertexFormatCompat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        //#else
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormatCompat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        //#endif

        //#if MC > 11502
        Matrix4f matrix4f = context.getMatrixStack().getPoseStack().last().pose();
        //#elseif MC > 11404
        //$$ Matrix4f matrix4f = new PoseStack().last().pose();
        //#endif

        float xWidth = 10.0f;
        float yWidth = 10.0f;
        float iconR = 1.0f;
        float iconG = 0.0f;
        float iconB = 0.0f;

        //#if MC > 12006
        //$$ bufferBuilder.addVertex(matrix4f, -xWidth, -yWidth, 0.0F).setUv(icon.getU0(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);
        //$$ bufferBuilder.addVertex(matrix4f, -xWidth, yWidth, 0.0F).setUv(icon.getU0(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
        //$$ bufferBuilder.addVertex(matrix4f, xWidth, yWidth, 0.0F).setUv(icon.getU1(), icon.getV1()).setColor(iconR, iconG, iconB, 0.5F);
        //$$ bufferBuilder.addVertex(matrix4f, xWidth, -yWidth, 0.0F).setUv(icon.getU1(), icon.getV0()).setColor(iconR, iconG, iconB, 0.5F);
        //$$ HighlightWaypointRenderer.end(bufferBuilder);
        //#elseif MC > 11404
        bufferBuilder.vertex(matrix4f, -xWidth, -yWidth, 0.0F).uv(icon.getU0(), icon.getV0()).color(iconR, iconG, iconB, 0.5F).endVertex();
        bufferBuilder.vertex(matrix4f, -xWidth, yWidth, 0.0F).uv(icon.getU0(), icon.getV1()).color(iconR, iconG, iconB, 0.5F).endVertex();
        bufferBuilder.vertex(matrix4f, xWidth, yWidth, 0.0F).uv(icon.getU1(), icon.getV1()).color(iconR, iconG, iconB, 0.5F).endVertex();
        bufferBuilder.vertex(matrix4f, xWidth, -yWidth, 0.0F).uv(icon.getU1(), icon.getV0()).color(iconR, iconG, iconB, 0.5F).endVertex();
        tesselator.end();
        //#else
        //$$ bufferBuilder.vertex(-xWidth, -yWidth, 0.0F).uv(icon.getU0(), icon.getV0()).color(iconR, iconG, iconB, 0.5F).endVertex();
        //$$ bufferBuilder.vertex(-xWidth, yWidth, 0.0F).uv(icon.getU0(), icon.getV1()).color(iconR, iconG, iconB, 0.5F).endVertex();
        //$$ bufferBuilder.vertex(xWidth, yWidth, 0.0F).uv(icon.getU1(), icon.getV1()).color(iconR, iconG, iconB, 0.5F).endVertex();
        //$$ bufferBuilder.vertex(xWidth, -yWidth, 0.0F).uv(icon.getU1(), icon.getV0()).color(iconR, iconG, iconB, 0.5F).endVertex();
        //$$ tesselator.end();
        //#endif
    }

    //#if MC > 12006
    //$$ private static void end(BufferBuilder builder) {
    //$$     try (MeshData meshData = builder.buildOrThrow()) {
    //$$         BufferUploader.drawWithShader(meshData);
    //$$     } catch (Exception ignore) {
    //$$     }
    //$$ }
    //#endif
}
