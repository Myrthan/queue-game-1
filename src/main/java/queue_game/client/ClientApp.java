package queue_game.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import queue_game.ActionCreator;
import queue_game.Updater;
import queue_game.controller.Game;
import queue_game.creator.LocalGameActionCreator;
import queue_game.model.DeckOfDeliveryCards;
import queue_game.model.DecksOfQueuingCardsBoxInterface;
import queue_game.model.DeliveryCard;
import queue_game.model.GameAction;
import queue_game.model.GameActionType;
import queue_game.model.GameState;
import queue_game.model.QueuingCard;
import queue_game.server.PlayerConnection;
import queue_game.server.Utilities;
import queue_game.view.JGameArea;
import queue_game.view.JPlayerList;

/**
 * Main window of game.
 * 
 */
public class ClientApp implements ActionCreator, DeckOfDeliveryCards, DecksOfQueuingCardsBoxInterface, Updater{
	private JGameArea gameArea;
	private JClientPlayerList playerList;
	private GameState gameState;
	private LocalGameActionCreator localCreator;
	private Game game;
	private Socket connection = null;
	private Reader in;
	private Writer out;
	private PlayerConnection player;
	private String name;
	private int nPlayers = 0;
	private int playerId;
	private int cardsLeft = 0;
	private String[] names = new String[5];

	public ClientApp() {
		String host = "127.0.0.1";
		try {
			connection = new Socket(host, 17373);
			in = new InputStreamReader(connection.getInputStream());
			out = new OutputStreamWriter(connection.getOutputStream());
			player = new PlayerConnection(connection);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Random r = new Random();
		name = Arrays.asList("Adam", "Bob", "Carl", "Dan", "Eve").get(
				r.nextInt(5))
				+ r.nextInt(100);
		System.out.println( "Wylosowane imię " + name);
		try {
			Utilities.writeObject(out, "NAME " + name + " ");
			Utilities.finishWriting(out);
			int tables = Utilities.readInt(in);
			while(tables-- > 0)
				Utilities.readInt(in);
			Utilities.writeObject(out, "JOIN 0 ");
			Utilities.finishWriting(out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		gameState = new GameState(Arrays.asList(name));

		localCreator = new LocalGameActionCreator(gameState);

		playerList = new JClientPlayerList(gameState, this);
		gameArea = new JGameArea(gameState, localCreator);
		localCreator.addView(playerList);
		localCreator.addView(gameArea);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					GameAction action;
					try {
						action = GameAction.read(in);
						
						handleAction(action);
						if(action.getType() == GameActionType.START_GAME)
							break;
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}
			}

		}).start();
		/*game = new Game(gameState, localCreator, null);
		game.addView(gameArea);
		game.addView(playerList);
		gameArea.setGame(game);
		game.startGame(5, this);*/
	}

	private void handleAction(GameAction action) {
		System.out.println(action);
		if(action.getType() == GameActionType.JOIN){
			nPlayers++;
			int id = (Integer)action.getInfo()[0];
			String name = (String)action.getInfo()[1];
			addPlayer(id, name);
		}
		if(action.getType() == GameActionType.START_GAME){
			startGame();
		}
	}
	private void updatePlayers(){
		List<String> namesList = new LinkedList<String>();
		for(int i = 0; i < nPlayers; i++)
			namesList.add(names[i]);
		gameState.resetPlayers(namesList);
		playerList.update();
		
	}
	private void startGame() {
		game = new Game(gameState, this, this);
		game.addView(gameArea);
		game.addView(playerList);
		gameArea.setGame(game);
		updatePlayers();
		game.startGame(nPlayers, this, this);
	}

	private void addPlayer(int id, String name){
		names[id] = name;
		if(name.equals(this.name))
			playerId = id;
		updatePlayers();
		
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("FIAO");
		frame.setPreferredSize(new Dimension(800, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		// gameState.addView(gameArea);
		// gameState.addView(playerList);

		frame.getContentPane().add(gameArea);
		frame.getContentPane().add(playerList, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		new ClientApp();
	}

	public GameAction getAction() throws InterruptedException {
		if(gameState.getActivePlayer() == playerId)
			return localCreator.getAction();
		try {
			return GameAction.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<QueuingCard> getCardsToFillTheHandOfPlayer(int playerNr) {
		LinkedList<QueuingCard> cards = new LinkedList<QueuingCard>();
		if(playerNr == playerId){
			if(gameState.getDayNumber() % 5 == 0 && cardsLeft < 10)
				cardsLeft = 10;
			while(cards.size() + gameState.getPlayer(playerId).getCardsOnHand().size() < 3 && cardsLeft-- > 0){
				GameAction action;
				try {
					action = GameAction.read(in);
				if(action.getType() != GameActionType.DRAW_CARD)
					throw new RuntimeException("Expected queuing cards: " + action);
					cards.add((QueuingCard) action.getInfo()[1]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cards;
	}

	public boolean hasCard(int playerNr, QueuingCard card) {
		if(playerNr == playerId)
			return gameState.getPlayer(playerId).getCardsOnHand().contains(card);
		return false;
	}

	public QueuingCard remove(int player) {
		if(player != playerId)
			return null;
		try {
			GameAction action = getAction();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public void resetAllDecks() {
		
	}

	/**
	 * @param gameAction
	 */
	public void sendAction(GameAction gameAction) {
		try {
			gameAction.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void update(GameAction action) {
		playerList.update();
		gameArea.update();
		System.out.println("UPDATE: " + action);
		if(action.getType() == GameActionType.ERROR)
			throw new RuntimeException("Sending ERROR");
		if(action.getType() == GameActionType.DRAW_CARD)
			return;
		if(action.getType() == GameActionType.CARDS_PEEKED)
			return;
		if(action.getType() == GameActionType.PRODUCT_DELIVERED)
			return;
		if((Integer)action.getInfo()[0] != playerId)
			return;
		try {
			action.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Collection<DeliveryCard> removeThreeCards() {
		GameAction action = null;
		try {
			action = getAction();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<DeliveryCard> result = new LinkedList<DeliveryCard>();
		result.add((DeliveryCard) action.getInfo()[0]);
		result.add((DeliveryCard) action.getInfo()[1]);
		result.add((DeliveryCard) action.getInfo()[2]);
		return result;
	}

	public Collection<DeliveryCard> peekTwoCards() {
		GameAction action = null;
		try {
			action = getAction();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<DeliveryCard> result = new LinkedList<DeliveryCard>();
		result.add((DeliveryCard) action.getInfo()[0]);
		result.add((DeliveryCard) action.getInfo()[1]);
		return result;
	}

	public void fill() {
	}
}
