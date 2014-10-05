import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class AnonClassExample {
	void foo() {
		// omp parallel
		{
			JButton jb = new JButton("button");
			jb.addActionListener(new ActionListener(){

				private int field;

				public void actionPerformed(ActionEvent e) {
					// omp parallel
					{
						ActionEvent tmpE = e;
						int tmpField = field;
						int tmpField2 = this.field;

						JButton jb2 = new JButton("foo");
						jb2.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e) {}
						});
					}
				}
			});
		}
	}
}
