/**
 * 
 */
package queue_game.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import queue_game.View;
import queue_game.model.DeckOfCards;
import queue_game.model.GamePhase;
import queue_game.model.GameState;
import queue_game.model.Player;
import queue_game.model.ProductType;
import queue_game.model.QueuingCard;
import queue_game.model.Store;

/**
 * @author michal
 * 
 *         Controller of game. Handles all players actions, verifies them,
 *         according to current game phase.
 */
public class Game implements Runnable {
	private GameState gameState;
	private int nPlayers;
	private List<View> views = new LinkedList<View>();
	private Class<?> expectedType = null;
	private ProductType selectedQueue = null;
	private Thread gameThread = null;
	private QueuingCard selectedQueuingCard = null;
	private boolean iPass[] = new boolean[6];
	private boolean pass = false;

	public Game() {
		gameState = new GameState();
	}

	/**
	 * Creates new thread for the game.
	 */
	public void startGame(int nPlayers) {
		gameState.reset(nPlayers);
		this.nPlayers = nPlayers;
		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * All Phases of all days.
	 */
	public void run() {
		try {
			PreparingToGamePhase();
			for (int day = 0; !gameOver(); day++) {
				gameState.setDayNumber(day);
				if (day != 0){
					queuingUpPhase();
					prepareToQueueJumping();
				}
				deliveryPhase();
				queueJumpingPhase();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return;
				}
				openingStoresPhase();
			}
			gameState.setGameOver();
			updateViews();
		} catch (InterruptedException e) {
			return;
		}
	}

	/**
	 * 
	 * Prepares game to GamePhase: players,products,pawns, shopping_list.
	 * @throws InterruptedException 
	 * 
	 */
	public void PreparingToGamePhase() throws InterruptedException {
		gameState.reset(nPlayers);
		gameState.resetNumberOfProducts();
		gameState.resetPlayers();
		gameState.resetCards();
		gameState.resetShoppingList();
		queuingUpPhase();
		for (ProductType pt : ProductType.values()) {
			gameState.putPawnofSpeculator(pt);
		}
	}

	/**
	 * First Phase of Day. Players select queues to place their pawns while
	 * there are any pawns left.
	 * 
	 * @throws InterruptedException
	 */
	private void queuingUpPhase() throws InterruptedException {
		gameState.setCurrentGamePhase(GamePhase.QUEUING_UP);
		int timeSinceLastPawnLocation = 0;
		outer: while (true) {
			for (int player = gameState.getGameOpeningMarker(); player < gameState
					.getNumberOfPlayers(); player = (player + 1)
					% gameState.getNumberOfPlayers()) {
				if (gameState.getNumberOfPawns(player) > 0) {
					gameState.setActivePlayer(player);
					gameState.putPlayerPawn(player, requestQueue());
					timeSinceLastPawnLocation = 0;
				} else {
					timeSinceLastPawnLocation++;
				}
				if (timeSinceLastPawnLocation > gameState.getNumberOfPlayers())
					break outer;
			}
		}
	}

	/**
	 * Second Phase of Day. Randomizes 3 stores with repetitions and delivers
	 * products to them and decreases number of products.
	 * 
	 * @author krzysiek & Helena
	 */
	public void deliveryPhase() {
		int rand;
		Random rG = new Random();
		for (int i = 0; i < 3; i++) {
			rand = rG.nextInt(5);
			ProductType type = ProductType.values()[rand];
			Store deliveredStore = gameState.getStore(type);
			deliveredStore.addProducts(1);
			Integer numberOfProducts[] = gameState.getNumberOfProducts();
			numberOfProducts[type.ordinal()] = numberOfProducts[type.ordinal()] - 1;
			gameState.setNumberOfProducts(numberOfProducts);
		}
		updateViews();
	}

	/**
	 * Third Phase of Day. Each player either plays card or passes. Phase is
	 * over when all players have passed or there are no cards left.
	 */
	public void queueJumpingPhase() {
		gameState.setCurrentGamePhase(GamePhase.JUMPING);
		final int numOfPlayers = gameState.getNumberOfPlayers();
		ArrayList<QueuingCard> cardsOnHand;
		QueuingCard current;
		while (true) {
			boolean allPassed = true;
			for (int player = gameState.getGameOpeningMarker(), i = 0; i < numOfPlayers; i++, player = (player + 1)
					% numOfPlayers) {
				cardsOnHand = this.getGameState().getPlayersList().get(player)
						.getCardsOnHand();
				DeckOfCards myDeck = this.getGameState().getDeck(player);
				if (!iPass[player] && cardsOnHand.size() > 0) {
					gameState.setActivePlayer(player);
					current = requestQueuingCard();
					if (current == null) {
						myDeck.iPass(cardsOnHand);
						continue;
					}
					cardsOnHand.remove(current);
					allPassed = false;
					switch (current) {
					case CLOSED_FOR_STOCKTAKING:
						System.out.println("CLOSED");
						break;
					case COMMUNITY_LIST:
						try{
							Collections.reverse(gameState.getStore(requestQueue()).getQueue());
						}catch(InterruptedException e){
							System.out.println(e);
						}
						
						System.out.println("USING COMMUNITY LIST");
						break;
					case CRITISIZING_AUTHORITIES:
						System.out.println("AUTHORITIES");
						break;
					case DELIVERY_ERROR:
						System.out.println("DELIVERY");
						break;
					case INCREASED_DELIVERY:
						System.out.println("INCREASED");
						break;
					case LUCKY_STRIKE:
						System.out.println("LUCKY");
						break;
					case MOTHER_WITH_CHILD:
						System.out.println("Mother");
						break;
					case NOT_YOUR_PLACE:
						System.out.println("PLACE");
						break;
					case TIPPING_FRIEND:
						System.out.println("TIPING");
						break;
					case UNDER_THE_COUNTER_GOODS:
						System.out.println("GOODS");
						break;
					default:
						break;

					}
				}
			}
			if (allPassed) {
				System.out.println("all");
				break;
			}
		}

		// NA KONCU ODSWIEŻYC LISTE IPASS[]. I ZEBY WSZYSTKO STYKALO
		for (int i = 0; i < 6; i++) {
			iPass[i] = false;
		}
	}

	/**
	 * Fourth Phase of Day. For each store with products, removes the right
	 * amount of product and pawns.
	 * 
	 * @author Jan
	 */
	public void openingStoresPhase() {
		for (ProductType type : ProductType.values())
			while (gameState.getStore(type).getQueue().size() > 0
					&& gameState.getStore(type).getNumberOf() > 0)
				gameState.sell(type);
		updateViews();
	}

	/**
	 * Waits for selection of queue by active player and returns selected queue.
	 * 
	 * @return destination of selected queue.
	 * @throws InterruptedException
	 */
	private synchronized ProductType requestQueue() throws InterruptedException {
		expectedType = ProductType.class;
		updateViews();
		while (selectedQueue == null)
			wait();
		expectedType = null;
		ProductType queue = selectedQueue;
		selectedQueue = null;
		return queue;

	}

	/**
	 * Waits for selection of queue by active player and returns selected queue.
	 * 
	 * @return destination of selected queue.
	 * @throws InterruptedException
	 */
	public synchronized QueuingCard requestQueuingCard() {
		expectedType = QueuingCard.class;
		updateViews();
		while (selectedQueuingCard == null && !pass) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		expectedType = null;
		QueuingCard card = selectedQueuingCard;
		selectedQueuingCard = null;
		pass = false;
		return card;
	}

	/**
	 * @return true if game is over and false otherwise.
	 */
	private boolean gameOver() {
		return gameState.getDayNumber() > 5;
	}

	/**
	 * Method for handling players' queue selections (e.g. on queuing up)
	 * 
	 * @param playerNo
	 *            ID of player selecting a product
	 * @param product
	 *            queue destination type or null, when destination is outdoor
	 *            market.
	 */
	public synchronized void queueSelected(int playerNo, ProductType destination) {
		if (playerNo != gameState.getActivePlayer())
			return;

		if (expectedType == ProductType.class) {

			selectedQueue = destination;
			try {
				notifyAll();
				return;
			} finally {
			}
		}
	}

	public synchronized void queuingCardSelected(int playerNo, QueuingCard card) {
		if (playerNo != gameState.getActivePlayer())
			return;
		if (expectedType == QueuingCard.class) {
			if (card == null) {
				iPass[playerNo] = true;
				pass = true;
			}
			selectedQueuingCard = card;
			try {
				return;
			} finally {
				notifyAll();
			}
		}
	}

	/**
	 * Sets object to be informed about changes in model;
	 */
	public void addView(View view) {
		views.add(view);
	}

	/**
	 * Informs all views about changes in model.
	 */
	private void updateViews() {
		for (View view : views)
			view.update();
	}

	/**
	 * @return state of the game (model).
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * @return main thread of game.
	 */
	public Thread getGameThread() {
		return gameThread;
	}
	private void prepareToQueueJumping(){
		for (Player p : gameState.getPlayersList()) {
			p.getDeck().getCards(p.getCardsOnHand());
		}
	}
	

}
