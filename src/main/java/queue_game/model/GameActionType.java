package queue_game.model;

/**
 * Enum containing all actions that may happen when playing
 * 
 * @author Szymon
 */
public enum GameActionType {
	START_GAME, DRAW_CARD, PAWN_PLACED, PRODUCT_DELIVERED,
	CARD_PLAYED, CARD_PLAYED_PASSED, CARDS_PEEKED, PRODUCT_BOUGHT,
	PAWN_REMOVED, PAWN_REMOVED_PASSED, PRODUCT_EXCHANGED_ONE,
	PRODUCT_EXCHANGED_TWO, PRODUCT_EXCHANGED_PASSED, CHAT
}
