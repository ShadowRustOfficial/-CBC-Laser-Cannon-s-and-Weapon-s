package com.example.colorcannons.client;

import com.example.colorcannons.config.ColorCannonsConfig;
import com.example.colorcannons.munitions.LaserAutocannonProjectile;
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
import rbasamoyai.createbigcannons.munitions.autocannon.AbstractAutocannonProjectile;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;
import rbasamoyai.createbigcannons.utils.CBCUtils;

/**
 * Short projectile tracer renderer.
 *
 * This intentionally does NOT render a continuous muzzle-to-target beam.
 * Each shot is a real CBC projectile and this renderer draws only a short,
 * fast-moving bolt/streak behind the projectile, giving the Star-Wars-style
 * projectile-tracer appearance while leaving CBC responsible for collision,
 * penetration and authoritative damage.
 *
 * The glow uses a normal translucent emissive RenderType. It is compatible
 * with Veil when Veil is installed, but the addon does not hard-depend on
 * Veil or touch its renderer internals, so a missing/outdated Veil cannot
 * crash the mod.
 */
public class ColorTracerRenderer<T extends AbstractAutocannonProjectile> extends AutocannonProjectileRenderer<T> {
    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("colorcannons", "textures/entity/beam.png");
    private static final RenderType BOLT = RenderType.entityTranslucentCull(BEAM_TEXTURE);

    private final int r;
    private final int g;
    private final int b;

    public ColorTracerRenderer(EntityRendererProvider.Context context, ModColorModes colorMode) {
        super(context);
        int color = colorMode.getTracerColor();
        r = (color >> 16) & 0xFF;
        g = (color >> 8) & 0xFF;
        b = color & 0xFF;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight) {
        Vec3 velocity = entity.getOrientation();
        if (velocity.lengthSqr() < 1.0E-6) {
            velocity = entity.getDeltaMovement();
        }
        if (velocity.lengthSqr() < 1.0E-6) {
            velocity = new Vec3(0, 0, 1);
        }
        velocity = velocity.normalize();

        // Short bolt length: it scales with projectile speed, but is capped so
        // the projectile never turns into a solid beam across the world.
        float length = (float) Math.max(0.18,
                Math.min(1.35, entity.getDeltaMovement().length() * 0.55))
                * ColorCannonsConfig.TRACER_LENGTH_MULTIPLIER.get().floatValue();
        float thickness = 0.045f * ColorCannonsConfig.TRACER_SIZE_MULTIPLIER.get().floatValue();

        poseStack.pushPose();
        orient(poseStack, velocity);
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer consumer = buffers.getBuffer(BOLT);

        // A bright narrow core and a softer outer streak. Both are short and
        // move with the projectile, rather than connecting muzzle to target.
        drawBolt(consumer, pose, r, g, b, length, thickness);
        drawBolt(consumer, pose,
                Math.min(255, r + 45), Math.min(255, g + 45), Math.min(255, b + 45),
                length * 0.92f, thickness * 1.9f);
        poseStack.popPose();

        // First two client ticks: draw the muzzle flash at the projectile's
        // recorded spawn point. It is deliberately a brief flash, not a beam.
        if (entity instanceof LaserAutocannonProjectile laser && entity.tickCount <= 2) {
            Vec3 muzzleOffset = laser.getMuzzleOrigin().subtract(entity.position());
            poseStack.pushPose();
            poseStack.translate(muzzleOffset.x, muzzleOffset.y, muzzleOffset.z);
            orient(poseStack, velocity);
            Matrix4f flashPose = poseStack.last().pose();
            drawBolt(consumer, flashPose,
                    Math.min(255, r + 70), Math.min(255, g + 70), Math.min(255, b + 70),
                    0.34f, thickness * 3.5f);
            poseStack.popPose();
        }
    }

    private static void orient(PoseStack poseStack, Vec3 velocity) {
        Vec3 horizontal = new Vec3(velocity.x, 0, velocity.z);
        if (horizontal.lengthSqr() > 1.0E-6 && Math.abs(velocity.y) > 0.01) {
            horizontal = horizontal.normalize();
            poseStack.mulPose(CBCUtils.mat4x4fFacing(velocity, horizontal));
            poseStack.mulPose(CBCUtils.mat4x4fFacing(horizontal, new Vec3(0, 0, -1)));
        } else {
            poseStack.mulPose(CBCUtils.mat4x4fFacing(velocity, new Vec3(0, 0, -1)));
        }
    }

    private static void drawBolt(VertexConsumer builder, Matrix4f pose,
            int r, int g, int b, float length, float thickness) {
        float x1 = -thickness;
        float y1 = -thickness;
        float z1 = -length;
        float x2 = thickness;
        float y2 = thickness;
        float z2 = 0.04f;

        quad(builder, pose, r, g, b, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
        quad(builder, pose, r, g, b, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        quad(builder, pose, r, g, b, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
        quad(builder, pose, r, g, b, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2);
        quad(builder, pose, r, g, b, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
        quad(builder, pose, r, g, b, x1, y1, z2, x1, y1, z1, x2, y1, z1, x2, y1, z2);
    }

    private static void quad(VertexConsumer builder, Matrix4f pose,
            int r, int g, int b,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {
        vertex(builder, pose, r, g, b, x1, y1, z1);
        vertex(builder, pose, r, g, b, x2, y2, z2);
        vertex(builder, pose, r, g, b, x3, y3, z3);
        vertex(builder, pose, r, g, b, x4, y4, z4);
    }

    private static void vertex(VertexConsumer builder, Matrix4f pose,
            int r, int g, int b, float x, float y, float z) {
        builder.addVertex(pose, x, y, z)
                .setColor(r, g, b, 255)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0, 1, 0);
    }

    @Override
    public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum,
            double x, double y, double z) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return BEAM_TEXTURE;
    }
}
