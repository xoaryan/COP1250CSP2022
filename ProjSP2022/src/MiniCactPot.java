import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;
public class MiniCactPot extends JFrame {
	// Components
	private JButton reset;
	private JPanel rewardsPanel,numberPanel;
	private JButton[][] numbers; 
	private JTextArea rewardsTextArea;
	private NumberMatrix nm;
	private Icon[] numberIcons;
	private Icon rightArrow,downArrow,diagArrow,invArrow,noIcon;
	private int maxUncover;
	private boolean beenSelected,arrowEnabled;
	
	public MiniCactPot() {
		// Text Area for Rules
		rewardsTextArea=new JTextArea(createRewardsTextArea());
		rewardsTextArea.setAlignmentX(CENTER_ALIGNMENT);
		rewardsTextArea.setAlignmentY(CENTER_ALIGNMENT);
		// Creating matrix for game
		nm=new NumberMatrix();
		// Setting buttons
		reset=new JButton("RESET");
		numberIcons=new Icon[9];
		rightArrow=new ImageIcon("rightarrow.png");
		downArrow=new ImageIcon("downarrow.png");
		diagArrow=new ImageIcon("dirdiag.png");
		invArrow=new ImageIcon("invdiag.png");
		noIcon=new ImageIcon("noicon.png");
		for(int i=0;i<9;i++)
			numberIcons[i]=new ImageIcon("number"+(i+1)+".png");
		
		rewardsPanel=new JPanel();
		numberPanel=new JPanel();
		numberPanel.setLayout(new GridLayout(5,5));
		numbers=new JButton[5][5];
		int x=0;
		// Creating numbers and non number buttons
		for(int i=0;i<5;i++)
			for(int j=0;j<5;j++) {
				numbers[i][j]=new JButton("");
				if(!((i==4 && (j>=0 && j<=4)) || (j==4 && (i>=1 && i<=4)) )   ) 
					numbers[i][j].setIcon(noIcon);
				numberPanel.add(numbers[i][j]);
				if(i!=0 && j!=0 && j!=4 && i!=4)
					numbers[i][j].addActionListener(new NumberListener());
			}
		// Setting Arrow buttons for rows and columns
		for(int cell=0;cell<3;cell++) {
			numbers[0][cell+1].setIcon(downArrow);
			numbers[0][cell+1].setName(String.valueOf(10+cell));
			numbers[0][cell+1].addActionListener(new ArrowListener());
			numbers[cell+1][0].setIcon(rightArrow);
			numbers[cell+1][0].setName(String.valueOf(20+cell));
			numbers[cell+1][0].addActionListener(new ArrowListener());
			numbers[0][cell+1].setBackground(Color.LIGHT_GRAY);
			numbers[cell+1][0].setBackground(Color.LIGHT_GRAY);
		}
		// Setting diagonal arrows
		numbers[0][0].setIcon(diagArrow);
		numbers[0][4].setIcon(invArrow);
		numbers[0][0].setName(String.valueOf(30));
		numbers[0][4].setName(String.valueOf(31));
		numbers[0][0].addActionListener(new ArrowListener());
		numbers[0][4].addActionListener(new ArrowListener());
		numbers[0][0].setBackground(Color.LIGHT_GRAY);
		numbers[0][4].setBackground(Color.LIGHT_GRAY);
		// Resetting matrix
		nm.resetMatrix(nm.getMatrix());
		// Passing numbers to buttons
		passNumbers();	
		// allowing arrows to work
		arrowEnabled=true;
		// disabling reset button
		reset.setEnabled(false);
		// Creating rewards panel with rewards textArea and reset button
		rewardsPanel.setLayout(new BorderLayout());
		rewardsPanel.add(rewardsTextArea,BorderLayout.CENTER);
		rewardsPanel.add(reset,BorderLayout.NORTH);
		this.setLayout(new GridLayout(1,2));
		// Adding panels to window
		add(numberPanel);
		add(rewardsPanel);
		reset.addActionListener(e->{//listener for reset button	
			resetGame();
		});
		
		// initial uncover of number
		randUncover();
		maxUncover++;
		// Setting window to be shown
		this.pack();
		setSize(1000,1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Game");
		setVisible(true);
	}
	
	/*
	 * Passing numbers from matrix to buttons
	 */
	public void passNumbers() {
		int iconval;
		for(int row=0;row<3;row++) {
			for(int col=0;col<3;col++) {
				iconval=nm.getMatrix()[row][col];
				numbers[row+1][col+1].setDisabledIcon(numberIcons[iconval-1]);
			}
		}
	}
	/*
	 * Random uncovering of number that is hidden
	 */
	public void randUncover() {
		Random rng=new Random();
		int num;
		boolean openCell=false;
		do {
			num=rng.nextInt(9);
			if(!isOpen(num)) {
				numbers[dialRow(num)][dialCol(num)].setEnabled(false);
				openCell=true;
			}
				
		}while(!openCell);
	}
	/*
	 * Checking for number to be open
	 */
	public boolean isOpen(int num) {
		return !numbers[dialRow(num)][dialCol(num)].isEnabled();
	}
	// Auxiliary methods for isOpen
	public int dialRow(int num) {
		return num/3+1;
	}
	
	public int dialCol(int num) {
		return num%3+1;
	}
	
	/*
	 * Listener for Numbers' buttons
	 */

	
	private class NumberListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b=(JButton) e.getSource();
			if(maxUncover<4) {
				b.setEnabled(false);
				maxUncover++;
			}
		}
		
	}
	/*
	 *  Listener for arrow buttons
	 */
	private class ArrowListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b=(JButton) e.getSource();
			String bName=b.getName();
			int bNumber=Integer.parseInt(bName);
			if(maxUncover==4 && !beenSelected && arrowEnabled) {
				b.setBackground(Color.RED);
				int sum=0;
				for(int row=0;row<3;row++) {
					for(int col=0;col<3;col++) {
						
						numbers[row+1][col+1].setEnabled(false);
					}
				}
				if(bNumber>=10 && bNumber<20) {
					
					int index=(bNumber-10)%3;
					for(int i=0;i<3;i++)
						sum+=nm.getMatrix()[i][index];
				}
				else if(bNumber>=20 && bNumber<30) {
					
					int index=(bNumber-20)%3;
					for(int i=0;i<3;i++)
						sum+=nm.getMatrix()[index][i];
				}
				else {
					
					if(bNumber==30)
						sum=nm.getMatrix()[0][0]+nm.getMatrix()[1][1]+nm.getMatrix()[2][2];
					if(bNumber==31)
						sum=nm.getMatrix()[0][2]+nm.getMatrix()[1][1]+nm.getMatrix()[2][0];
				}
				for(int row=1;row<=3;row++) {
					
					numbers[row][4].setFont(new Font("Arial", Font.PLAIN, 24));
					
					
					numbers[row][4].setText(String.valueOf(nm.getRowSums(nm.getMatrix(), row-1)));
				}
					
					
				for(int col=1;col<=3;col++) {
					numbers[4][col].setFont(new Font("Arial", Font.PLAIN, 24));
					
					numbers[4][col].setText(String.valueOf(nm.getColSums(nm.getMatrix(), col-1)));
				}
				
				numbers[4][4].setFont(new Font("Arial", Font.PLAIN, 24));
				numbers[4][4].setText(String.valueOf(nm.getDirDiagSum(nm.getMatrix())));	
				numbers[4][0].setFont(new Font("Arial", Font.PLAIN, 24));
				numbers[4][0].setText(String.valueOf(nm.getInvDiagSum(nm.getMatrix())));
				arrowEnabled=false;
				JOptionPane.showMessageDialog(null,String.valueOf(sum));
				reset.setEnabled(true);
			}
				
		}
		
	}
	
	
	
	/*
	 *  Resetting the game in UI
	 */	
	public void resetGame() {
		nm.resetMatrix(nm.getMatrix());
		//nm.showMatrix();
		maxUncover=0;
		beenSelected=false;
		for(int i=0;i<4;i++)
			for(int j=0;j<5;j++) {				
				numbers[i][j].setEnabled(true);
			}
		passNumbers();
		randUncover();
		maxUncover++;
		for(int cell=0;cell<3;cell++) {
			numbers[0][cell+1].setBackground(Color.LIGHT_GRAY);
			numbers[cell+1][0].setBackground(Color.LIGHT_GRAY);
		}
		// Setting diagonal arrows
		numbers[0][0].setBackground(Color.LIGHT_GRAY);
		numbers[0][4].setBackground(Color.LIGHT_GRAY);
		for(int cell=0;cell<3;cell++) {
			numbers[0][cell+1].setBackground(Color.LIGHT_GRAY);
			numbers[cell+1][0].setBackground(Color.LIGHT_GRAY);
		}
		// Setting diagonal arrows
		numbers[0][0].setBackground(Color.LIGHT_GRAY);
		numbers[0][4].setBackground(Color.LIGHT_GRAY);
		for(int row=1;row<=3;row++) 
			numbers[row][4].setText("");
			
		for(int col=1;col<=3;col++) 	
			numbers[4][col].setText("");
		
		numbers[4][4].setText("");	
		
		numbers[4][0].setText("");
		arrowEnabled=true;
		reset.setEnabled(false);
	}
	
	/*
	 * main method
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MiniCactPot();
	}
	
	/*
	 *  STARTING THE SECTION THAT THE STUDENT HAS TO CODE.
	 *  READ CAREFULLY THE DESCRIPTION AND ASK QUESTIONS
	 *  IF NECESSARY
	 */
	
	/*
	 *  Methods 1. Create a String that has a table of rewards
	 *  Make sure that contains the information shown in
	 *  the demo. It does not need to be 100% with the
	 *  same spacing and/or format as the demo. 
	 */
	/*
	 *STARTING THE SECTION THAT THE STUDENT HAS TO CODE.
	 *  READ CAREFULLY THE DESCRIPTION AND ASK QUESTIONS
	 *  IF NECESSARY
	 */

	public String createRewardsTextArea() {
	  // Returns the proper string for the rewards table above
	  return "*******PAYOUT*******\n"
       	 	+ "Sum\tMGP\tSum\tMGP\n"
        	+ "6\t10000\t16\t72\n"
        	+ "7\t36\t17\t180\n"
        	+ "8\t720\t18\t119\n"
        	+ "9\t360\t19\t36\n"
        	+ "10\t80\t20\t306\n"
        	+ "11\t252\t21\t1080\n"
        	+ "12\t108\t22\t144\n"
        	+ "13\t72\t23\t1800\n"
        	+ "14\t54\t24\t3600\n"
        	+ "15\t180";
				
	}

	private class NumberMatrix{
  		private int[][] matrix;
		private final int ROWNUMS=3,COLNUMS=3;
		public NumberMatrix() {
			matrix=new int[ROWNUMS][COLNUMS];
		}
		
		//resets the matrix
		public void resetMatrix(int[][] matrix) {
      		// Initialize a new matrix
			int[][] newMatrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
      
      			Random rg = new Random();
			// Switches a number for a random other number
			for (int i=0;i<newMatrix.length;i++)
			  for(int j=0;j<newMatrix[i].length;j++) { 
                             int randomRow;
                             int randomColumn;
                             int saved = newMatrix[i][j];
                             randomRow = rg.nextInt(3);
                             randomColumn = rg.nextInt(3);
                             newMatrix[i][j]=newMatrix[randomRow][randomColumn];
                             newMatrix[randomRow][randomColumn] = saved;
                          }
      
                        // Copy new matrix to original
                       for (int i=0;i<3;i++)
                         for(int j=0;j<3;j++)
                           matrix[i][j]=newMatrix[i][j];
      
		}

		public int[][] getMatrix(){
			return matrix;
		}
		
                /*
		 *  For the matrix and an specific row to return
		 *  the sum of the numbers in that row
		 */
		public int getRowSums(int[][] matrix,int row) {
			// Add code and return the proper number
                        int sum=0;
                        for(int i=0; i<3; i++)
                          sum +=matrix[row][i];
			
                        return sum;
		}
		
                /*
		 *  For the matrix and an specific column to return
		 *  the sum of the numbers in that column
		 */
		public int getColSums(int[][] matrix,int col) {
			// Add code and return the proper number
			int sum=0;
                        for(int i=0; i<3; i++)
                          sum +=matrix[i][col];
			
                        return sum;
		}
		
                /*
		 *  For the matrix return the sum of the direct
		 *  diagonal
		 */
		public int getDirDiagSum(int[][] matrix) {
			// Add code and returns the proper number
			int sum = 0;
                        for(int i=0; i<3; i++)
                          sum+=matrix[i][i];  
      
                        return sum;
		}
		
                /*
		 *  For the matrix return the sum of the inverse
		 *  diagonal
		 */
		public int getInvDiagSum(int[][] matrix) {
			int sum = 0;
                        for(int i=0; i<3; i++)
                          sum+=matrix[i][2-i];			
      
                        return sum;
		}
		
	}

}
