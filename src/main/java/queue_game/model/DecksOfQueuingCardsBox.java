package queue_game.model;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class DecksOfQueuingCardsBox implements DecksOfQueuingCardsBoxInterface {
	private StandardDeckOfQueuingCards[] decks = new StandardDeckOfQueuingCards[5];
	private StandardDeckOfQueuingCards[] onHands = new StandardDeckOfQueuingCards[5];
	private GameState gameState;
	
	
	public DecksOfQueuingCardsBox(GameState gameState){
		this.gameState = gameState;
		for (int i=0; i<5; i++)
		{
			decks[i] = new StandardDeckOfQueuingCards();
			onHands[i] = new StandardDeckOfQueuingCards(true);
		}
	}
	
	public List<QueuingCard> getCardsOnHand(int playerNr) {
		return onHands[playerNr].getDeck();
	}
	/*
	 * After reorganisation i see no reason why filling hand should not be
	 * internal operation of this class. But if one does, he can uncomment it. 
	 
	public List<QueuingCard> getCardsToFillTheHandOfPlayer(int playerNr){
		int amount = 3 - onHands[playerNr].size();
		List<QueuingCard> res = new LinkedList<QueuingCard>();
		try {
			while(amount > 0){
				res.add(decks[playerNr].remove());
				amount--;
			}
		}
		catch (NoSuchElementException e){
			//nothing
		}
		return res;
	}
	*/
	
	public void fillTheHand(int playerNr){
		int amount = 3 - onHands[playerNr].size();
		List<QueuingCard> temp = new LinkedList<QueuingCard>();
		try {
			while(amount > 0){
				temp.add(decks[playerNr].remove());
				amount--;
			}
		}
		catch (NoSuchElementException e){
			//nothing
		}
		onHands[playerNr].addAll(temp);
		}
	
	public boolean hasAnyCardOnTheHand(int playerNr){
		return onHands[playerNr].size()!=0;
	}
	
	public boolean hasCardOnTheHand(int playerNr, QueuingCard card){
		return onHands[playerNr].hasCard(card);
	}
	
	public boolean removeFromHand(int playerNr, QueuingCard card){
		return onHands[playerNr].remove(card);
	}
	
	public QueuingCard removeFromDeck(int player){
		return decks[player].remove();
	}
	
	public void resetAll(){
		for (int i=0; i<gameState.getNumberOfPlayers(); i++)
		{
			decks[i].reset();
			onHands[i] = new StandardDeckOfQueuingCards(true);
		}
	}
	
}
