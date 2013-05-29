package queue_game.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
//import java.util.Arrays;

/**
 * @author Piotr, everything changed by krzysiek
 * Deck of Cards
 */
public class StandardDeckOfQueuingCards{
	private LinkedList<QueuingCard> deck=new LinkedList<QueuingCard>();
	//private List<QueuingCard> cardsOnHand=new ArrayList<QueuingCard>();

	/**
	 * 
	 * @return
	 *  Takes card from the top of the deck, and gives it to "on hand" list
	 */
	
	public StandardDeckOfQueuingCards() {
		reset();
	}
	
	public StandardDeckOfQueuingCards(boolean empty) {
		if(!empty)
			reset();
	}
	
	
	public List<QueuingCard> getDeck(){
		return deck;
	}
	
	public void addAll(List<QueuingCard> list)
	{
		deck.addAll(list);
	}
	/*public List<QueuingCard> getCardsToFillTheHandOfPlayer(int amount){
		//for (int i = 0; i < 3 && cardsOnHand.size() < 3; i++) {
		//	if(size()==0)
		//		break;
		//	cardsOnHand.add(deck.get(deck.size() - 1));
		//	deck.remove(deck.size() - 1);
		//}
		List<QueuingCard> res = new LinkedList<QueuingCard>();
		while(amount-- > 0) {
			if(size()==0) break;
			res.add(deck.removeFirst());
		}
		return res;
	}
	*/
	
	/**
	 * 
	 * @return
	 * returns the size of the deck
	 */
	public int size(){
		return deck.size();
	}
	/**
	 * Shuffles the deck
	 */
	private void shuffle(){
		Collections.shuffle(deck);
	}
	/**
	 * fills the deck of cards
	 */
	private void fill(){
		/*for (int i=0; i<10; i++)
			deck.add(QueuingCard.COMMUNITY_LIST);*/
		
		deck.addAll(Arrays.asList(QueuingCard.values()));
		
	}
	public void reset(){
		deck = new LinkedList<QueuingCard>();
		fill();
		shuffle();
	}
	
	public QueuingCard remove(){
		return deck.remove();
	}
	
	public boolean remove(QueuingCard card){
		return deck.remove(card);
	}
	
	public boolean hasCard(QueuingCard card){
		System.out.println("KK: " + deck.contains(card) + " " + card);
		System.out.println(deck);
		return deck.contains(card);
	}

}



/**
 * @author krzysiek
 * Removes the first occurrence of the specified element from this list,
 * if it is present.
 * If this list does not contain the element, it is unchanged. 
 * @return true if this list contained the specified element
 
public boolean remove(QueuingCard queuingCard){
	return deck.remove(queuingCard);
}*/

/**
 * @author krzysiek
 * A function similar to addAll function. 
 */
/*
public void addListToTheEnd(List<QueuingCard> list){
	try {
		deck.addAll(list);
	} catch (NullPointerException e){
		//nothing
	}
}
*/
