package queue_game.model;

import java.util.List;

public interface DecksOfQueuingCardsBoxInterface {
	//public List<QueuingCard> getCardsToFillTheHandOfPlayer(int playerNr);
	
	public List<QueuingCard> getCardsOnHand(int playerNr);
	public void fillTheHand(int playerNr);
	public boolean hasCardOnTheHand(int playerNr, QueuingCard card);
	public QueuingCard removeFromDeck(int player);
	public boolean removeFromHand(int playerNr, QueuingCard card);
	public void resetAll();
	public boolean hasAnyCardOnTheHand(int playerNr);
	
	
}
