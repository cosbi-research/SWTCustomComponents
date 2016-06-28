package swtcc.custom.swt.widgets;

public class MinMaxValidatorExample implements IPopupLinkInputValidator {

	private int minValue, maxValue;
	
	
	public MinMaxValidatorExample(int min, int max){
		this.minValue = min;
		this.maxValue = max;
	}
	
	@Override
	public String validateInput(String value, String[] items, int selectedIndex) {
		
		String msg = null;
		
		try {
			Double dval = Double.parseDouble(value);
			if (dval < 0 && selectedIndex != 3)
				msg = "A non-negative number is needed";
			
			if(minValue >= dval && selectedIndex != 3)
				msg = "the number must be > "+ minValue;
			
			if(maxValue <= dval && selectedIndex != 3)
				msg = "the number must be < "+ maxValue;
			
			System.out.println(msg);
			
			return msg;
		} catch (Exception ex1) {
			msg = "A float number is needed";
			return msg;
		}
	}

}
