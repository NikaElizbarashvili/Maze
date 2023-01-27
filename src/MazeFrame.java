import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class MazeFrame extends JFrame{
	public MazePanel panel;
	
	public MazeFrame() {
		panel=new MazePanel();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, 0);
		this.setResizable(false);
		this.add(panel);
		this.pack();
		this.setVisible(true);		
	}
	
}
