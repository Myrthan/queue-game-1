/**
 * 
 */
package queue_game.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import queue_game.controller.Game;
import queue_game.creator.LocalGameActionCreator;
import queue_game.model.GameState;

/**
 * @author michal
 *
 */
public class JGameArea extends JPanel implements View{
	
	private static final long serialVersionUID = -1161332361871600948L;
	
	private Dimension defaultSize = new Dimension(800, 600);
	private ProductPanel productPanel;
	private JBoard board;
	private JCardsArea cardsArea;

	private Game game;
	public JGameArea(GameState gameState, LocalGameActionCreator creator){
		super();
		this.productPanel = new ProductPanel(gameState);
		this.board = new JBoard(gameState, creator);
		this.cardsArea = new JCardsArea(gameState);
		add(productPanel, BorderLayout.PAGE_START);
		add(board, BorderLayout.CENTER);
		add(cardsArea, BorderLayout.PAGE_END);
	}
	public void setGame(Game game){
		board.setGame(game);
		cardsArea.setGame(game);
	}
	public void update() {
		productPanel.repaint();
		board.update();
		cardsArea.update();
		
	}

}
