package neil.demo.mayday2018;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fx implements Serializable {

	private String base;
	private String quote;
	private double price;
	private Date effective;
	
	/**
	 * <p>Canonical form of the currency pair, "EUR/USD" for Euro against the Dollar, etc
	 * </p>
	 */
	public String getPair() {
		return this.base.toUpperCase() + "/" + this.quote.toUpperCase();
	}
}
