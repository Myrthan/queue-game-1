package queue_game.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A class which every object contains all the data about the particular player. 
 * 
 * @author krzysiek
 *
 */
public class Player {
	private int numberOfPawns;
	private List<Integer> shoppingList = null;
	private int[] boughtProducts = {0, 0, 0, 0, 0};
	//private List<QueuingCard> cardsOnHand=new LinkedList<QueuingCard>();
	private String name;
	private int ID;
	
	/**
	 * 
	 * @param index from 0 to number of players-1
	 * @param name
	 */
	public Player(int ID, String name) {
		this.name = name;
		this.ID = ID;
	}
	
	public String getName(){
		return name;
	}
	
	public int getID(){
		return ID;
	}
	/**
	 * 
	 * @param list an array with the number of needed products from
	 * all stores. The array inside the class will be a clone of this array.
	 * 
	 * @throws IllegalArgumentException if argument's length differs
	 * ProductType.values().length
	 */
	public void setShoppingList(List<Integer> list){
		if(list.size() != ProductType.values().length)
			throw new IllegalArgumentException();
		for(Integer i : list)
			if(i < 0 || i > 4)
				throw new IllegalArgumentException();
		this.shoppingList = list;
	}
	
	public List<Integer> getShoppingList(){
		return shoppingList;
	}
	
	/*public void setCardsOnHand(List<QueuingCard> cards){
		cardsOnHand=cards;
	}
	public List<QueuingCard> getCardsOnHand(){
		return cardsOnHand;
	}
	public void addCardsToHand(List<QueuingCard> newCards){
		cardsOnHand.addAll(newCards);
	}
	*/	
	public void addPawn(){
		numberOfPawns++;
	}
	public void setNumberOfPawns(int numberOfPawns) {
		this.numberOfPawns = numberOfPawns;
	}
	
	public int getNumberOfPawns(){
		return numberOfPawns;
	}
	
	
	public void addProduct(ProductType type){
		boughtProducts[type.ordinal()]++;
	}
	
	public int[] getBoughtProducts() {
		return boughtProducts;
	}
	/**
	 * 
	 * @param ProductType
	 * @author Jan
	 */
	public void removeProduct(ProductType type){
		if(boughtProducts[type.ordinal()] > 0)
			boughtProducts[type.ordinal()]--;
		else
			throw new IllegalArgumentException("Player doesn't have such product.");
	}
	/**
	 * 
	 * @param Collection<ProductType>
	 * @author Jan
	 */
	public void removeProducts(Collection<ProductType> typeCollection){
		int[] products = {0,0,0,0,0};
		for(ProductType i: typeCollection)
			products[i.ordinal()]++;
		
		for(int i = 0; i < products.length; i++){
			if(products[i] > boughtProducts[i])
				throw new IllegalArgumentException("Player doesn't have such product.");
		}
		
		for(int i = 0; i < products.length; i++)
			boughtProducts[i] -= products[i];
	}
}
