package ces.riccico.common.enums;

public enum Amenities {

	AC((byte) 0b00000100), 			// decimal : 4
	
	FRIDGE((byte) 0b00000010), 		// decimal : 2
	
	SWIM_POOL((byte) 0b00000001),	// decimal : 1
	
	TIVI((byte) 0b00001000), 		// decimal : 8
	
	WIFI((byte) 0b00010000); 		// decimal : 16

	private byte value;

	Amenities(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
