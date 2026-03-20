// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelhard_hat<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "hard_hat"), "main");
	private final ModelPart Head;
	private final ModelPart Strap;

	public Modelhard_hat(ModelPart root) {
		this.Head = root.getChild("Head");
		this.Strap = this.Head.getChild("Strap");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -9.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.25F)).texOffs(0, 12)
						.addBox(-4.0F, -9.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.5F)).texOffs(0, 24)
						.addBox(-4.0F, -5.0F, -8.0F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Strap = Head.addOrReplaceChild("Strap",
				CubeListBuilder.create().texOffs(24, 0)
						.addBox(-4.0F, -5.0F, 4.0F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 28)
						.addBox(-4.0F, -5.0F, -4.0F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(20, 24)
						.addBox(4.0F, -5.0F, -4.0F, 0.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}