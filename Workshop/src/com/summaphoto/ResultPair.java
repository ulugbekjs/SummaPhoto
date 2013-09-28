package com.summaphoto;

import Common.Photo;

class ResultPair {
	boolean validCollage = false;
	Photo collage = null;
	private int diffHorizontal = 0;
	private int diffVertical = 0;

	public int getDiffHorizontal() {
		return diffHorizontal;
	}

	public int getDiffVertical() {
		return diffVertical;
	}

	ResultPair(boolean valid, Photo collage) {
		this.validCollage = valid;
		this.collage = collage;
	}
	
	ResultPair(boolean valid, Photo collage, int diffH, int diffV) {
		this.validCollage = valid;
		this.collage = collage;
		this.diffHorizontal = diffH;
		this.diffVertical = diffV;
	}
}
