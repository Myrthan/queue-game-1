/**
 * 
 */
package queue_game.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author michal A model part of MVC. Illustrates current situation on Board.
 */
public class GameState {

	/**
	 * Counted from 0.
	 */
	public static final Color[] productColors = new Color[] {
			new Color(0x5DC049), new Color(0xFFB451), new Color(0xDF574E),
			new Color(0x57ACB0), Color.MAGENTA };

	/**
	 * Counted from 0
	 */
	public static final Color[] playerColors = new Color[] {
			new Color(0xff0000), new Color(0xffff00), new Color(0, 128, 0),
			new Color(192, 128, 0), new Color(0x0670C7) };

	private int dayNumber;
	private int gameOpeningMarker;
	private int numberOfPlayers;
	private int activePlayer = 0;
	private boolean gameOver;
	private Store[] stores;
	private int[] numberOfProductsLeft = new int[5];
	private ArrayList<Player> players = new ArrayList<Player>();
	private GamePhase currentGamePhase = null;
	private ArrayList<GameAction> actions = new ArrayList<GameAction>();
	private Collection<DeliveryCard> currentDeliveryList;
	private Store market;
	private String message = "";

	public GameState(List<String> names) {
		stores = new Store[ProductType.values().length];
		int ind = 0;
		for (ProductType product : ProductType.values())
			stores[ind++] = new Store(product);
		market = new Store(null);
		resetPlayers(names);
	}

	/**
	 * @param names
	 * @param arrayList
	 */
	public void initGame(
			List<List<Integer>> shoppingLists) {
		if (players.size() != shoppingLists.size())
			throw new IllegalArgumentException(
					"Diffrent number of player names and shopping lists");
		reset(players.size());
		resetNumberOfProductsLeft();
		resetShoppingList(shoppingLists);
	}

	private synchronized void reset(int nPlayers) {
		dayNumber = 0;
		gameOpeningMarker = 0;
		activePlayer = 0;
		numberOfPlayers = nPlayers;
		currentGamePhase = null;
		gameOver = false;
		stores = new Store[ProductType.values().length];
		int ind = 0;
		for (ProductType product : ProductType.values())
			stores[ind++] = new Store(product);
		market = new Store(null);
	}

	
	/*	/**
	 * It doesn't work yet!
	 * 
	 * Resets cards of all players due to saturday rules, which means that cards
	 * which a player is now holding on hand are to be set on the end of the
	 * lst.
	 */
	/**
	 * 
	 * Resets number of products with our favorite number.
	 * 
	 */
	private synchronized void resetNumberOfProductsLeft() {
		for (ProductType i : ProductType.values()) {
			numberOfProductsLeft[i.ordinal()] = 12;
		}
	}

	/**
	 * 
	 * Create new players and adds them pawns.
	 * @param names 
	 * 
	 */
	public synchronized void resetPlayers(List<String> names) {
		players.clear();
		int initialNumberOfPawns = 5;
		int ind = 0;
		for(String name : names){
			Player player = new Player(ind++, name);
			player.setNumberOfPawns(initialNumberOfPawns);
			players.add(player);
		}
	}

	/**
	 * 
	 * Create random shopping list and set gameOpeningMarker.
	 * @param shoppingLists 
	 * 
	 */
	private synchronized void resetShoppingList(List<List<Integer>> shoppingLists) {
		Iterator<List<Integer>> it = shoppingLists.iterator();
		for(Player player : players)
			player.setShoppingList(it.next());
		this.setGameOpeningMarker(0);
	}

	public synchronized void setGameOver() {
		gameOver = true;
	}

	public synchronized boolean isGameOver() {
		return gameOver;
	}

	/**
	 * @return the gameOpeningMarker
	 */
	public synchronized int getGameOpeningMarker() {
		return gameOpeningMarker;
	}

	/**
	 * @param gameOpeningMarker
	 *            the gameOpeningMarker to set
	 */
	public synchronized void setGameOpeningMarker(int gameOpeningMarker) {
		this.gameOpeningMarker = gameOpeningMarker;
	}

	/**
	 * @return the dayNumber
	 */
	public synchronized int getDayNumber() {
		return dayNumber;
	}

	/**
	 * @param dayNumber
	 *            the dayNumber to set
	 */
	public synchronized void setDayNumber(int dayNumber) {
		this.dayNumber = dayNumber;
	}

	/**
	 * @return the stores
	 */
	public synchronized Store[] getStores() {
		return stores;
	}

	/**
	 * @return the market
	 */
	public synchronized Store getOutDoorMarket() {
		return market;
	}

	public synchronized Store getStore(ProductType product) {
		if (product == null)
			return market;
		return stores[product.ordinal()];
	}

	public synchronized int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public synchronized GamePhase getCurrentGamePhase() {
		return currentGamePhase;
	}

	public synchronized void setCurrentGamePhase(GamePhase phase) {
		currentGamePhase = phase;
	}

	public synchronized void setCurrentDeliveryList(
			Collection<DeliveryCard> tempDCList) {
		this.currentDeliveryList = tempDCList;
	}

	public synchronized Collection<DeliveryCard> getCurrentDeliveryList() {
		return currentDeliveryList;
	}

	/**
	 * @return ID of player whose turn is now.
	 */
	public synchronized int getActivePlayer() {
		return activePlayer;
	}

	/**
	 * Sets ID of player whose turn is now.
	 */
	public synchronized void setActivePlayer(int id) {
		if(id < -1 || id >= numberOfPlayers)
			throw new IllegalArgumentException("No such player: " + id);
		activePlayer = id;
	}

	public synchronized int getNumberOfPawns(int player) {
		return players.get(player).getNumberOfPawns();
	}

	public synchronized int[] getNumberOfProductsLeft() {
		return numberOfProductsLeft;
	}

	public synchronized int getNumberOfProductsLeft(int numberOfStore) {
		return numberOfProductsLeft[numberOfStore];
	}

	public synchronized void setNumberOfProductsLeft(int[] numberOfProductsLeft) {
		this.numberOfProductsLeft = numberOfProductsLeft;
	}

	public synchronized void setNumberOfProductsLeft(int index,
			int numberOfProductsLeft) {
		this.numberOfProductsLeft[index] = numberOfProductsLeft;
	}

	public synchronized ArrayList<Player> getPlayersList() {
		return players;
	}
	
	public synchronized Player getPlayer(int i){
		return players.get(i);
	}

	public synchronized void setPlayersList(ArrayList<Player> players) {
		this.players = players;
	}

	public synchronized void addGameAction(GameAction action) {
		actions.add(action);
	}

	public synchronized List<GameAction> getGameActions() {
		return actions;
	}

	public void close(ProductType type) {
		getStore(type).setClosed(true);
	}

	public void reverse(ProductType type) {
		Collections.reverse(getStore(type).getQueue());
	}

	/**
	 * 
	 * Set stores open.
	 * 
	 */
	public void openStores() {
		for (ProductType p : ProductType.values()) {
			getStore(p).setClosed(false);
		}
	}

	/**
	 * 
	 * @param orig - Store from product has been taken
	 * @param dest - store where product is give
	 * @param product - type of product which is move
	 */
	public void transferToAnotherStore(ProductType orig,ProductType dest, ProductType product){
		getStore(orig).removeProduct(product);
		getStore(dest).addProduct(product);
	}

	/**
	 * Moves pawn.
	 * 
	 * @param orig
	 *            - original store of this pawn
	 * @param pos
	 *            - position in original queue
	 * @param dest
	 *            - destination of pawn
	 * @param npos
	 *            - new position of pawn
	 */
	public void movePawn(ProductType orig, int pos, ProductType dest, int npos) {
		Integer pawn = getStore(orig).getQueue().remove(pos);
		getStore(dest).getQueue().add(npos, pawn);
	}

	/**
	 * 
	 * Puts black pawns of speculators to all queues.
	 * 
	 */
	public synchronized void putSpeculators() {
		for (Store store : stores)
			store.getQueue().add(-1);
	}

	/**
	 * Transfers given amount of product to corresponding store.
	 * 
	 * @param product
	 *            type of delivered product
	 * @throws IllegalArgumentException
	 *             too large amount of product.
	 */
	public void transferProductToStore(ProductType product, int amount)
			throws IllegalArgumentException {
		if (numberOfProductsLeft[product.ordinal()] < amount)
			throw new IllegalArgumentException(
					"No more pieces of product left: " + product);
		numberOfProductsLeft[product.ordinal()] -= amount;
		stores[product.ordinal()].addProducts(amount);
	}

	/**
	 * Puts pawn of one player to given queue
	 * 
	 * @param player
	 * @param destination
	 */

	public synchronized void putPlayerPawn(int player, ProductType destination) {
		if (player < 0 || player >= numberOfPlayers)
			throw new IllegalArgumentException("No such Player: " + player);
		int nPawns = getNumberOfPawns(player);
		if (nPawns == 0)
			throw new IllegalArgumentException("Player has no more pawns: "
					+ player);
		players.get(player).setNumberOfPawns(nPawns - 1);
		this.getStore(destination).getQueue().add(player);
	}

	/**
	 * 
	 */
	public void removePlayerPawn(int position, ProductType destination) {
		if(position < 0 || this.getStore(destination).getQueue().size() <= position)
			throw new IllegalArgumentException("No such position in queue: " + position);
		int player = this.getStore(destination).getQueue().get(position);
		int nPawns = players.get(player).getNumberOfPawns();
		players.get(player).setNumberOfPawns(nPawns + 1);
		this.getStore(destination).getQueue().remove(position);
	}

	/**
	 * @param type
	 * 
	 */
	public synchronized void sell(ProductType offeredProduct,
			ProductType soldProduct) throws IllegalArgumentException {
		if (getStore(offeredProduct).getQueue().isEmpty())
			throw new IllegalArgumentException("Empty queue: " + offeredProduct);
		int player = getStore(offeredProduct).getQueue().peek();
		if(player != -1)
			getStore(offeredProduct).removeProduct(soldProduct);

		if (player >= 0 && player < numberOfPlayers) {
			getStore(offeredProduct).getQueue().remove();

			players.get(player).addPawn();
			players.get(player).addProduct(soldProduct);
		}
		if (player == -1) {
			int queueLength = getStore(offeredProduct).getQueue().size();
			movePawn(offeredProduct, 0, offeredProduct, queueLength - 1);
			transferToAnotherStore(offeredProduct, null, soldProduct);
		}
	}

	/**
	 * Method for outdoormarket.
	 * 
	 * @author Jan
	 */
	public synchronized boolean trade(ProductType soldProduct,
			Collection<ProductType> offeredProducts)
			throws IllegalArgumentException {

		int player = getActivePlayer();
		if(getOutDoorMarket().getNumberOf(soldProduct) == 0)
			return false;
		//throw new IllegalArgumentException("Not enough products in market");
		try{
			players.get(player).removeProducts(offeredProducts);	
		}catch(IllegalArgumentException e){
			return false;
		}
		
		getOutDoorMarket().removeProduct(soldProduct);
		players.get(player).addPawn();
		players.get(player).addProduct(soldProduct);
		for(ProductType i: offeredProducts)
			getOutDoorMarket().addProduct(i);
		return true;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean wasDelivered(ProductType type) {
		for (DeliveryCard card : currentDeliveryList)
			if (card.getProductType().equals(type))
				return true;
		return false;
	}

	/**
	 * @return
	 */
	public ProductType getCheapProduct() {
		return ProductType.values()[dayNumber % 5];
	}
}
