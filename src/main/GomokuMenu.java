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
	private JButton computerButton;

	private static final int WIDTH = 350;
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
		add(getComputerButton());
	}

	public JButton getHumanButton() {
		if (humanButton == null) {
			humanButton = new JButton("Human vs human");
			humanButton.addActionListener(actionListener);
		}
		return humanButton;
	}

	public JButton getComputerButton() {
		if (computerButton == null) {
			computerButton = new JButton("Human vs computer");
			computerButton.addActionListener(actionListener);
		}
		return computerButton;
	}

	private ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getHumanButton()) {
				setVisible(false);
				GomokuMainBoard newBoard = new GomokuMainBoard(false);
				newBoard.setVisible(true);
			} else if (e.getSource() == getComputerButton()) {
				GomokuMainBoard newBoard = new GomokuMainBoard(true);
				newBoard.setVisible(true);
			}
		}
	};
}
