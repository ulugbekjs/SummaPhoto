package ActivationManager;

import Common.Photo;

class ResultPair {
	boolean validCollage = false;
	Photo collage = null;

	ResultPair(boolean valid, Photo collage) {
		this.validCollage = valid;
		this.collage = collage;
	}
}
