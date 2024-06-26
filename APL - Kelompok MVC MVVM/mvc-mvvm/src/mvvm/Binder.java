package mvvm;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Binder {

	private Object source = null;

	public Binder(JSpinner spinner, ViewModelProperty property) {
        if (property != null) { // Check if property is not null
            property.setModelViewPropertyChangeEvent(new ViewModelPropertyChangeEvent() {
                @Override
                public void onPropertyChanged(ViewModelProperty modelViewProperty) {
                    if (source instanceof JSpinner) {
                        source = null;
                        return;
                    }
                    source = modelViewProperty;
                    spinner.setValue(modelViewProperty.getValue());
                }
            });

		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (source instanceof ViewModelProperty) {
					source = null;
					return;
				}
				source = spinner;
				property.setValue(spinner.getValue());
				property.getViewModel().onPropertyChanged(property);
			}
		});
	}
	}
}
