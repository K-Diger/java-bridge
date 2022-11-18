package bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeGameStarter {

    private final Map<String, Boolean> gameStatusFlag = new HashMap<>();
    private final InputView inputView;
    private final OutputView outputView;
    private final BridgeMaker bridgeMaker;
    private final BridgeGame bridgeGame;

    private List<String> bridge = new ArrayList<>();
    private List<String> result = new ArrayList<>();
    private int tryCount = 1;

    public BridgeGameStarter(InputView inputView, OutputView outputView, BridgeMaker bridgeMaker, BridgeGame bridgeGame) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.bridgeMaker = bridgeMaker;
        this.bridgeGame = bridgeGame;
    }

    public void initialize() {
        outputView.printGameStartContext();
        outputView.printInputBridgeSize();
        bridge = bridgeMaker.makeBridge(inputView.readBridgeSize());
        outputView.printInputMoving();
        result = bridgeGame.move(inputView.readMoving(), bridge, 0);
        outputView.printMap(result);
    }

    public void reInitialize() {
        outputView.printInputBridgeSize();
        outputView.printInputMoving();
        bridgeGame.initializeLeftRightMovingLog();
        result = bridgeGame.move(inputView.readMoving(), bridge, 0);
        outputView.printMap(result);
    }

    public int additionalInput(int index) {
        outputView.printInputMoving();
        result = bridgeGame.move(inputView.readMoving(), bridge, index);
        outputView.printMap(result);
        index++;
        return index;
    }

    public void notifySuccessGame() {
        gameStatusFlag.put("successFlag", true);
        gameStatusFlag.put("retryFlag", false);
    }

    public void notifyFailGameWithRetry() {
        gameStatusFlag.put("successFlag", false);
        gameStatusFlag.put("retryFlag", true);
    }

    public void notifyFailGameWithNotRetry() {
        gameStatusFlag.put("successFlag", false);
        gameStatusFlag.put("retryFlag", false);
    }

    public void work() {
        int index = 1;
        while (bridgeGame.canMove(result)) {
            if (bridgeGame.isGameEnded(bridge, index)) {
                notifySuccessGame();
                return;
            }
            index = additionalInput(index);
        }
        outputView.printInputGameCommand();
        if (bridgeGame.retry(inputView.readGameCommand())) {
            notifyFailGameWithRetry();
            return;
        }
        notifyFailGameWithNotRetry();
    }

    public void closeGame() {
        outputView.printFinalMap(result);
        outputView.printResult(gameStatusFlag.get("successFlag"), tryCount);
    }

    public void run() {
        initialize();
        work();
        while (gameStatusFlag.get("retryFlag")) {
            tryCount++;
            reInitialize();
            work();
        }
        closeGame();
    }
}
