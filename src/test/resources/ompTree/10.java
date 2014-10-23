import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AnonClassExample {
	void foo() {
		JButton jb = new JButton("button");
		jb.addActionListener(new ActionListener(){

			private int field;

			public void actionPerformed(ActionEvent e) {
				// omp parallel
				{
					ActionEvent tmpE = e;
					int tmpField = field;
					int tmpField2 = this.field;
				}
			}
		});
	}
}
