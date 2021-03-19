package ces.riccico.models;

import java.util.List;

public class PaginationModel {
	
	private int pageMax;
	
	private List<HouseModel> listHouse;

	public int getPageMax() {
		return pageMax;
	}

	public void setPageMax(int pageMax) {
		this.pageMax = pageMax;
	}

	public List<HouseModel> getListHouse() {
		return listHouse;
	}

	public void setListHouse(List<HouseModel> listHouse) {
		this.listHouse = listHouse;
	}

}
