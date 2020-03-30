package main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import board.GomokuMainBoard;

public class GomokuMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton humanButton;
	private JButton humanVsComputerButton;
	private JButton computerVsComputerButton;

	private static final int WIDTH = 550;
	private static final int HEIGHT = 80;

	public GomokuMenu() {
		super("Gomoku");
		initialize();
	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new Dimension(WIDTH, HEIGHT));
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
		setResizable(false);
		setLayout(new GridLayout());
		add(getHumanButton());
		add(getHumanVsComputerButton());
		add(getComputerVsComputerButton());
	}

	public JButton getHumanButton() {
		if (humanButton == null) {
			humanButton = new JButton("Human vs human");
			humanButton.addActionListener(actionListener);
		}
		return humanButton;
	}

	public JButton getHumanVsComputerButton() {
		if (humanVsComputerButton == null) {
			humanVsComputerButton = new JButton("Human vs computer");
			humanVsComputerButton.addActionListener(actionListener);
		}
		return humanVsComputerButton;
	}
	
	public JButton getComputerVsComputerButton() {
		if (computerVsComputerButton == null) {
			computerVsComputerButton = new JButton("Computer vs computer");
			computerVsComputerButton.addActionListener(actionListener);
		}
		return computerVsComputerButton;
	}

	private ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getHumanButton()) {
				setVisible(false);
				GomokuMainBoard newBoard = new GomokuMainBoard(GomokuMainBoard.HUMAN_VS_HUMAN);
				newBoard.setVisible(true);
			} else if (e.getSource() == getHumanVsComputerButton()) {
				GomokuMainBoard newBoard = new GomokuMainBoard(GomokuMainBoard.HUMAN_VS_COMPUTER);
				newBoard.setVisible(true);
			} else if (e.getSource() == getComputerVsComputerButton()) {
				GomokuMainBoard newBoard = new GomokuMainBoard(GomokuMainBoard.COMPUTER_VS_COMPUTER);
				newBoard.setVisible(true);
			}
		}
	};
}
