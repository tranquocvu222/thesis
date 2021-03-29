package ces.riccico.model;

import java.util.List;

public class PaginationModel {

	private int pageMax;

	private List<Object> listObject;

	public int getPageMax() {
		return pageMax;
	}

	public void setPageMax(int pageMax) {
		this.pageMax = pageMax;
	}

	public List<Object> getListObject() {
		return listObject;
	}

	public void setListObject(List<Object> listObject) {
		this.listObject = listObject;
	}

}
