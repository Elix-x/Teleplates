package code.elix_x.mods.iitemrenderer.api;

import java.lang.reflect.Method;

import com.google.common.base.Throwables;

import net.minecraft.item.Item;

/**
 * Main class of IItem Renderer API.
 * <br><br>
 * Here you register your {@link IItemRenderer}.
 * <br><br>
 * This API in whole allows use of GL calls during rendering, super-ultra-hyper dynamic rendering, and manipulations of GUI overlay.
 * <br><br>
 * This API is built entirely without ASM.
 * <br><br>
 * So now, you ask: "If this is so simple and possible without ASM, why can't i copy all your code to my project and don't use IItem Renderer API?".
 * <br>
 * Well, that's a good question, so here's your answer: if every body that wants to do this, does this with his own reflection block (even if copied from IItem Renderer), it will cause incompatibilities between every user of such code block, because of all the side effects it has.
 * @author elix_x
 *
 */
public class IItemRendererAPI {

	private static final Method registerIItemRenderer;

	static {
		try {
			registerIItemRenderer = Class.forName("code.elix_x.mods.iitemrenderer.render.IItemRendererHandler").getMethod("registerIItemRenderer", Item.class, IItemRenderer.class);
		} catch(Exception e){
			throw Throwables.propagate(e);
		}
	}

	/**
	 * Method to register your IItemRenderer
	 * @param item {@link Item} that this {@link IItemRenderer} handles
	 * @param renderer {@link IItemRenderer} that handles rendering of that item
	 */
	public static void registerIItemRenderer(Item item, IItemRenderer renderer){
		try {
			registerIItemRenderer.invoke(null, item, renderer);
		} catch(Exception e){
			throw Throwables.propagate(e);
		}
	}

}
