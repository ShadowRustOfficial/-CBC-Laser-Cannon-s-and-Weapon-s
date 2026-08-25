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
import com.example.colorcannons.munitions.LaserAutocannonProjectile;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * CBC's own {@code AutocannonProjectileRenderer} already renders tracer
 * rounds as a glowing elongated box beam (that's what {@code isTracer()}
 * triggers) — see its decompiled source for reference. Its color (a
 * hardcoded yellow/orange) is baked into private static helper methods we
 * can't call or override, so this class re-implements that same box-beam
 * technique — same vertex layout, same "inner beam + larger outer halo"
 * two-pass approach — parameterized on our own RGB per color mode and a
 * length/thickness multiplier read live from {@link ColorCannonsConfig},
 * instead of duplicating CBC's copyrighted texture/color asset.
 *
 * Works for both BLUE_TRACER and RED_TRACER — CBC's own
 * {@code APAutocannonProjectile} class is used directly as the entity
 * (registered under two different EntityTypes), so the only thing that
 * needs to differ per registration is which renderer instance (and which
 * RGB triple) gets bound — see {@link ColorCannonsClient}.
 */
public class ColorTracerRenderer<T extends AbstractAutocannonProjectile> extends AutocannonProjectileRenderer<T> {

    private static final ResourceLocation COLOR_LOCATION =
            ResourceLocation.fromNamespaceAndPath("colorcannons", "textures/entity/beam.png");
    private static final RenderType BEAM = RenderType.entityTranslucentCull(COLOR_LOCATION);

    private final int r, g, b;

    public ColorTracerRenderer(EntityRendererProvider.Context context, ModColorModes colorMode) {
        super(context);
        int color = colorMode.getTracerColor();
        this.r = (color >> 16) & 0xFF;
        this.g = (color >> 8) & 0xFF;
        this.b = color & 0xFF;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Vec3 current = entity.position();
        Vec3 origin = entity instanceof LaserAutocannonProjectile laser
                ? laser.getMuzzleOrigin()
                : current.subtract(entity.getOrientation().normalize().scale(0.5));
        Vec3 beam = current.subtract(origin);
        double beamLength = beam.length();
        if (beamLength < 0.05) {
            return;
        }

        float length = (float) Math.min(beamLength, 512.0);
        float thickness = 0.0625f * (float) (double) ColorCannonsConfig.TRACER_SIZE_MULTIPLIER.get().floatValue();
        Vec3 vel = ((AbstractCannonProjectile) entity).getOrientation();
        if (vel.lengthSqr() < 1.0E-4) {
            vel = beam.normalize();
        }

        poseStack.pushPose();
        if (vel.horizontalDistanceSqr() > 1.0E-4 && Math.abs(vel.y) > 0.01) {
            Vec3 horizontal = new Vec3(vel.x, 0.0, vel.z).normalize();
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize().reverse(), horizontal));
            poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal, new Vec3(0.0, 0.0, -1.0)));
        } else {
            poseStack.mulPose(CBCUtils.mat4x4fFacing(vel.normalize(), new Vec3(0.0, 0.0, -1.0)));
        }

        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f pose = lastPose.pose();
        VertexConsumer vcons = buffers.getBuffer(BEAM);

        // Bright core in this color, plus a slightly larger, dimmer halo behind it --
        // same two-pass approach CBC's own tracer uses, just recolored per mode.
        renderBox(vcons, pose, r, g, b, length, thickness);
        renderBoxInverted(vcons, pose, Math.min(255, r + 40), Math.min(255, g + 40), Math.min(255, b + 40), length, thickness * 1.5f);

        poseStack.popPose();
        // Deliberately does not call super.render(): the parent
        // AutocannonProjectileRenderer's render() would draw CBC's own
        // hardcoded yellow/orange tracer box a second time on top of ours
        // (isTracer() is always true for our rounds). This entity never
        // has a custom name tag or leash, so skipping EntityRenderer's
        // base render() body (which only handles those) is safe.
    }

    @Override
    public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
        return true; // tracer bolts should never frustum-cull out early, same as CBC's own tracers
    }

    private static void renderBox(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float length, float thickness) {
        float x1 = -thickness, y1 = -thickness, z1 = -thickness - length;
        float x2 = thickness, y2 = thickness, z2 = thickness;
        vertex(builder, pose, r, g, b, x1, y1, z1);
        vertex(builder, pose, r, g, b, x1, y2, z1);
        vertex(builder, pose, r, g, b, x2, y2, z1);
        vertex(builder, pose, r, g, b, x2, y1, z1);
        vertex(builder, pose, r, g, b, x1, y1, z1);
        vertex(builder, pose, r, g, b, x1, y1, z2);
        vertex(builder, pose, r, g, b, x1, y2, z2);
        vertex(builder, pose, r, g, b, x1, y2, z1);
        vertex(builder, pose, r, g, b, x1, y1, z2);
        vertex(builder, pose, r, g, b, x2, y1, z2);
        vertex(builder, pose, r, g, b, x2, y2, z2);
        vertex(builder, pose, r, g, b, x1, y2, z2);
        vertex(builder, pose, r, g, b, x2, y1, z1);
        vertex(builder, pose, r, g, b, x2, y2, z1);
        vertex(builder, pose, r, g, b, x2, y2, z2);
        vertex(builder, pose, r, g, b, x2, y1, z2);
    }

    private static void renderBoxInverted(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float length, float thickness) {
        float x1 = -thickness, y1 = -thickness, z1 = -thickness - length;
        float x2 = thickness, y2 = thickness, z2 = thickness;
        vertex(builder, pose, r, g, b, x1, y1, z1);
        vertex(builder, pose, r, g, b, x2, y1, z1);
        vertex(builder, pose, r, g, b, x2, y2, z1);
        vertex(builder, pose, r, g, b, x1, y2, z1);
        vertex(builder, pose, r, g, b, x1, y1, z1);
        vertex(builder, pose, r, g, b, x1, y2, z1);
        vertex(builder, pose, r, g, b, x1, y2, z2);
        vertex(builder, pose, r, g, b, x1, y1, z2);
        vertex(builder, pose, r, g, b, x1, y1, z2);
        vertex(builder, pose, r, g, b, x1, y2, z2);
        vertex(builder, pose, r, g, b, x2, y2, z2);
        vertex(builder, pose, r, g, b, x2, y1, z2);
        vertex(builder, pose, r, g, b, x2, y1, z2);
        vertex(builder, pose, r, g, b, x2, y2, z2);
        vertex(builder, pose, r, g, b, x2, y2, z1);
        vertex(builder, pose, r, g, b, x2, y1, z1);
    }

    private static void vertex(VertexConsumer builder, Matrix4f pose, int r, int g, int b, float x, float y, float z) {
        builder.addVertex(pose, x, y, z).setColor(r, g, b, 255).setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(0.0f, 1.0f, 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
