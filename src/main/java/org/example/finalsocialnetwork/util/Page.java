package org.example.finalsocialnetwork.util;

public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberElements;

    public Page(Iterable<E> elementsOnPage, int totalNumberElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberElements = totalNumberElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberElements() {
        return totalNumberElements;
    }


}
