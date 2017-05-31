package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.StarWarsGen;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//import org.dark.shaders.light.LightData;
//import org.dark.shaders.util.ShaderLib;
//import org.dark.shaders.util.TextureData;

public class SWRebalancedPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() throws ClassNotFoundException {

        try {
            Global.getSettings().getScriptClassLoader().loadClass("org.lazywizard.lazylib.ModUtils");
        } catch (ClassNotFoundException ex) {
            String message = System.lineSeparator()
                    + System.lineSeparator() + "LazyLib is required to run at least one of the mods you have installed."
                    + System.lineSeparator() + System.lineSeparator()
                    + "You can download LazyLib at http://fractalsoftworks.com/forum/index.php?topic=5444"
                    + System.lineSeparator();
            throw new ClassNotFoundException(message);
        }

        /*try {  //GraphicsLib
            Global.getSettings().getScriptClassLoader().loadClass("org.dark.shaders.util.ShaderLib");  
            ShaderLib.init();  
            LightData.readLightDataCSV("data/lights/SWRebalanced_bling.csv"); 
    //        TextureData.readTextureDataCSV("data/lights/SWRebalanced_bump.csv"); 
        } catch (ClassNotFoundException ex) {
        }  */
    }

    @Override
    public void onNewGame() {
        try {
            //Got Exerelin, so load Exerelin  
            Class<?> def = Global.getSettings().getScriptClassLoader().loadClass("exerelin.campaign.SectorManager");
            Method method;
            try {
                method = def.getMethod("getCorvusMode");
                Object result = method.invoke(def);
                if ((boolean) result == true) {
                    // Exerelin running in Corvus mode, go ahead and generate our sector  
                    new StarWarsGen().generate(Global.getSector());
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException ex) {
                // check failed, do nothing  
            }
        } catch (ClassNotFoundException ex) {
            new StarWarsGen().generate(Global.getSector());
            // Exerelin not found so continue and run normal generation code
        }
    }
/*
    @Override
    public void onNewGameAfterProcGen() {
        try {
            //Got Exerelin, so load Exerelin  
            Class<?> def = Global.getSettings().getScriptClassLoader().loadClass("exerelin.campaign.SectorManager");
            Method method;
            try {
                method = def.getMethod("getCorvusMode");
                Object result = method.invoke(def);
                if ((boolean) result == true) {
                    // Exerelin running in Corvus mode, go ahead and generate our sector  
                    new StarWarsGen().generate(Global.getSector());
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException ex) {
                // check failed, do nothing  
            }
        } catch (ClassNotFoundException ex) {
            new StarWarsGen().generate(Global.getSector());
            // Exerelin not found so continue and run normal generation code
        }
    }*/

}
