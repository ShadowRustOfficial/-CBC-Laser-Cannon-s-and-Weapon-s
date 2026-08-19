package com.example.colorcannons.client;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.registry.ModColorModes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.utils.CBCUtils;

public class ColorTracerRenderer<T extends AbstractAutocannonProjectile> extends AutocannonProjectileRenderer<T> {
    private static final ResourceLocation COLOR_LOCATION = ResourceLocation.fromNamespaceAndPath("colorcannons", "textures/entity/beam.png");
    private static final RenderType BEAM = RenderType.entityTranslucentCull(COLOR_LOCATION);
    private final int r, g, b;

    public ColorTracerRenderer(EntityRendererProvider.Context context, ModColorModes colorMode) {
        super(context);
        int color = colorMode.getTracerColor();
        r = (color >> 16) & 0xFF; g = (color >> 8) & 0xFF; b = color & 0xFF;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Vec3 previous = new Vec3(entity.xOld, entity.yOld, entity.zOld);
        Vec3 diff = entity.position().subtract(previous);
        double dlSqr = diff.lengthSqr();
        boolean fast = 1.0E-4 <= dlSqr && dlSqr <= entity.getDeltaMovement().lengthSqr() * 4.0;
        double diffLength = fast ? diff.length() : 0.0;
        double displacement = entity.getTotalDisplacement() - diffLength * (1.0f - partialTicks);
        float length = (float) Math.min(diffLength, displacement) * ColorCannonsConfig.TRACER_LENGTH_MULTIPLIER.get().floatValue();
        float thickness = 0.0625f * ColorCannonsConfig.TRACER_SIZE_MULTIPLIER.get().floatValue();

        Vec3 vel = ((AbstractCannonProjectile) entity).getOrientation();
        if (vel.lengthSqr() < 1.0E-4) vel = new Vec3(0.0, -1.0, 0.0);
        poseStack.pushPose();
        if (vel.horizontalDistanceSqr() > 1.0E-4 && Math.abs(vel.y) > 0.01) {
            Vec3 horizontal = new Vec3(vel.x, 0.0, vel.z).normalize();
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
            poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal, new Vec3(0.0, 0.0, -1.0)));
        } else {
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize(), new Vec3(0.0, 0.0, -1.0)));
        }
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer vcons = buffers.getBuffer(BEAM);
        renderBox(vcons, pose, r, g, b, length, thickness);
        renderBoxInverted(vcons, pose, Math.min(255, r + 40), Math.min(255, g + 40), Math.min(255, b + 40), length, thickness * 1.5f);
        poseStack.popPose();
    }

    @Override public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) { return true; }

    private static void renderBox(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float length, float thickness) {
        float x1=-thickness,y1=-thickness,z1=-thickness-length,x2=thickness,y2=thickness,z2=thickness;
        vertex(builder,pose,r,g,b,x1,y1,z1); vertex(builder,pose,r,g,b,x1,y2,z1); vertex(builder,pose,r,g,b,x2,y2,z1); vertex(builder,pose,r,g,b,x2,y1,z1);
        vertex(builder,pose,r,g,b,x1,y1,z1); vertex(builder,pose,r,g,b,x1,y1,z2); vertex(builder,pose,r,g,b,x1,y2,z2); vertex(builder,pose,r,g,b,x1,y2,z1);
        vertex(builder,pose,r,g,b,x1,y1,z2); vertex(builder,pose,r,g,b,x2,y1,z2); vertex(builder,pose,r,g,b,x2,y2,z2); vertex(builder,pose,r,g,b,x1,y2,z2);
        vertex(builder,pose,r,g,b,x2,y1,z1); vertex(builder,pose,r,g,b,x2,y2,z1); vertex(builder,pose,r,g,b,x2,y2,z2); vertex(builder,pose,r,g,b,x2,y1,z2);
    }

    private static void renderBoxInverted(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float length, float thickness) {
        float x1=-thickness,y1=-thickness,z1=-thickness-length,x2=thickness,y2=thickness,z2=thickness;
        vertex(builder,pose,r,g,b,x1,y1,z1); vertex(builder,pose,r,g,b,x2,y1,z1); vertex(builder,pose,r,g,b,x2,y2,z1); vertex(builder,pose,r,g,b,x1,y2,z1);
        vertex(builder,pose,r,g,b,x1,y1,z1); vertex(builder,pose,r,g,b,x1,y2,z1); vertex(builder,pose,r,g,b,x1,y2,z2); vertex(builder,pose,r,g,b,x1,y1,z2);
        vertex(builder,pose,r,g,b,x1,y1,z2); vertex(builder,pose,r,g,b,x1,y2,z2); vertex(builder,pose,r,g,b,x2,y2,z2); vertex(builder,pose,r,g,b,x2,y1,z2);
        vertex(builder,pose,r,g,b,x2,y1,z2); vertex(builder,pose,r,g,b,x2,y2,z2); vertex(builder,pose,r,g,b,x2,y2,z1); vertex(builder,pose,r,g,b,x2,y1,z1);
    }

    private static void vertex(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float x, float y, float z) {
        builder.addVertex(pose, x, y, z).setColor(r,g,b,255).setUv(0.0f,0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(0.0f,1.0f,0.0f);
    }

    @Override public ResourceLocation getTextureLocation(T entity) { return COLOR_LOCATION; }
}
