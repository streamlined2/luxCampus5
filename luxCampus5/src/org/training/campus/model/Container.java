package org.training.campus.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.training.campus.domain.Entity;

public class Container<K extends Comparable<K>,E extends Entity<K>> implements Iterable<E> {
	private Object[] data;
	private int size;
	
	public Container(int capacity) {
		if(capacity<=0) throw new IllegalArgumentException("initial capacity should be positive value");
		data=new Object[capacity];
		size=0;
	}
	
	private int getNewCapacity(int requestedCapacity) {
		return requestedCapacity*2;
	}
	
	private void ensureCapacity(int requestedCapacity) {
		if(requestedCapacity>data.length) {
			allocateCopyData(requestedCapacity);
		}
	}
	
	private void shrinkCapacity(int requestedCapacity) {
		if(getNewCapacity(requestedCapacity)<data.length) {
			allocateCopyData(requestedCapacity);			
		}
	}

	public void allocateCopyData(int requestedCapacity) {
		Object[] newData=new Object[getNewCapacity(requestedCapacity)];
		System.arraycopy(data, 0, newData, 0, size);
		data=newData;
	}
	
	public int indexOf(E e) {
		return indexOf(e.getId());
	}
	
	public int indexOf(K id) {
		int start = 0;
		int finish = size-1;
		while(start<=finish) {
			int index = (start+finish)/2;
			if(((E)data[index]).getId().compareTo(id)<0) {
				start=index+1;
			}else if(((E)data[index]).getId().compareTo(id)>0) {
				finish=index-1;
			}else {
				return index;
			}
		}
		return -(start+1);
	}
	
	public Stream<E> stream(){
		Builder<E> b=Stream.builder();
		for(int k=0;k<size;k++) {
			b.accept((E)data[k]);
		}
		return b.build();
	}
	
	public Optional<E> getById(K id) {
		int index=indexOf(id);
		return index>=0? Optional.ofNullable((E)data[index]): Optional.empty();
	}

	public void add(E e) {
		int index=indexOf(e);
		if(index>=0) throw new IllegalStateException("such element already exists in container");
		ensureCapacity(size+1);
		int placeAt=-index-1;
		System.arraycopy(data, placeAt, data, placeAt+1, size-placeAt);
		data[placeAt]=e;
		size++;
	}
	
	public E remove(K id) {
		int deleteAt=indexOf(id);
		if(deleteAt<0) throw new NoSuchElementException("no such element found");
		E toBeDeleted=(E)data[deleteAt];
		System.arraycopy(data, deleteAt+1, data, deleteAt, size-1-deleteAt);
		size--;
		shrinkCapacity(size);
		return toBeDeleted;		
	}
	
	public E remove(E e) {
		return remove(e.getId());
	}
	
	public E update(E e) {
		int updateAt=indexOf(e);
		if(updateAt<0) throw new NoSuchElementException("no such element found");
		E toBeUpdated=(E)data[updateAt];
		data[updateAt]=e;
		return toBeUpdated;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int index=0;

			@Override
			public boolean hasNext() {
				return index<size;
			}

			@Override
			public E next() {
				if(index>=size) throw new NoSuchElementException("check if there are elements left before advancing");
				return (E)(data[index++]);
			}
		};
	}
	
	@Override
	public String toString() {
		StringBuilder b=new StringBuilder();
		for(E e:this) {
			b.append(e.toString()).append('\n');
		}
		return b.toString();
	}
	
}