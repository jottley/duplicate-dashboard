package net.ottleys.duplicate.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

import net.ottleys.duplicate.dao.model.ContentEntity;

public class ContentEntityQueue extends AbstractQueue<ContentEntity> {

    private LinkedList<ContentEntity> internalList;

    public ContentEntityQueue() {
        this.internalList = new LinkedList<>();
    }

    @Override
    public boolean offer(ContentEntity contentEntity) {
        if (contentEntity == null) return false;
        internalList.add(contentEntity);
        return true;
    }

    @Override
    public ContentEntity poll() {
        Iterator<ContentEntity> iterator = internalList.iterator();
        ContentEntity contentEntity = iterator.next();
        if (contentEntity != null) {
            iterator.remove();
            return contentEntity;
        }

        return null;
    }

    @Override
    public ContentEntity peek() {
        return internalList.getFirst();
    }

    @Override
    public Iterator<ContentEntity> iterator() {
        return internalList.iterator();
    }

    @Override
    public int size() {
        return internalList.size();
    }
    
}
