import com.toxicrain.core.json.gameinfoParser;
import com.toxicrain.core.GameEngine;

public class Application {

    public static void main(String[] args) {
        gameinfoParser.loadGameInfo();
        GameEngine.run(gameinfoParser.defaultWindowName);
    }

}