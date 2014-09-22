import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

abstract class MyActionListener extends java.awt.Point implements ActionListener {
	public abstract void actionPerformed(ActionEvent e);
	public int inherField = 15;
}

class AnonClassExample {
	void foo() {
		JButton jb = new JButton("button");
		jb.addActionListener(new MyActionListener(){

			private int field;

			public void actionPerformed(ActionEvent e) {
				// omp parallel
				{
					ActionEvent tmpE = e;
					int tmpField = field;
					int tmpField2 = this.field;
					int tmpIF = inherField;
					int tmpIF2 = this.inherField;
				}
			}
		});
	}
}
