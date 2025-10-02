package cassetu.mystbornhorizons.mixin;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface ShaderAccessor {
    @Accessor("postProcessor")
    PostEffectProcessor getPostProcessor();

    @Accessor("postProcessor")
    void setPostProcessor(PostEffectProcessor processor);

    @Invoker("loadPostProcessor")
    void invokeLoadPostProcessor(Identifier id);
}