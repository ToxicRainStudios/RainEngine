import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.GameEngine;

public class Application {

    public static void main(String[] args) {
        GameInfoParser.loadGameInfo();
        GameEngine.run(GameInfoParser.defaultWindowName);
    }

}