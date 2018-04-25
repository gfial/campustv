package dbm.data.campus;

import java.util.Collection;
import java.util.LinkedList;

import controller.db.campus.TagController;
import data.campus.Tag;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.TransactionProcessingException;

public class TagPersistor {

	public static final int BASE_TAG = 1;
	public static final int EVENT_TAG = 2;
	
	public Collection<Tag> getTags(Collection<Tag> tags) throws TransactionProcessingException, NoSuchTagException {
		Collection<Tag> validTags = new LinkedList<Tag>();
		for(Tag tag: tags) {
			validTags.add(getTag(getTag(tag)));
		}
		return validTags;
	}
	
	public static Collection<Integer> getTagIds(Collection<Integer> tags) throws TransactionProcessingException, NoSuchTagException {
		Collection<Integer> validTags = new LinkedList<Integer>();
		for(Integer tag: tags) {
			validTags.add(getTag(getTag(tag)));
		}
		return validTags;
	}
	
	public static int getTag(Tag tag) throws NoSuchTagException {
		int tagId = tag.getId();
		try {
			Tag t = getTag(tagId);
			if(t == null) tagId = getTagId(tag.getName());
		} catch (TransactionProcessingException e) {
			try {
				tagId = getTagId(tag.getName());
			} catch (TransactionProcessingException e1) {
				throw new NoSuchTagException();
			}
		}
		return tagId;
	}
	
	public static Collection<Integer> getAllParents(int tag) throws TransactionProcessingException {
		Collection<Integer> tags = new LinkedList<Integer>();
		for (Integer parent : TagPersistor.getParents(tag)) {
			tags.addAll(getAllParentsRec(parent));
		}
		return tags;
	}
	
	/**
	 * Gets all the parents of the given tag.
	 * 
	 * @param tag
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<Integer> getAllParentsRec(int tag) throws TransactionProcessingException {
		Collection<Integer> tags = new LinkedList<Integer>();
		tags.add(tag);
		for (Integer parent : TagPersistor.getParents(tag)) {
			tags.addAll(getAllParents(parent));
		}
		return tags;
	}

	/**
	 * Gets all the childs of the given tag.
	 * 
	 * @param tag
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<Integer> getAllChilds(int tag) throws TransactionProcessingException {
		Collection<Integer> tags = new LinkedList<Integer>();
		tags.add(tag);
		for (int child : TagPersistor.getChilds(tag)) {
			tags.addAll(getAllChilds(child));
		}
		return tags;
	}

	public static Collection<Integer> getParents(int tag) throws TransactionProcessingException {
		return TagController.getParents(tag);
	}

	public static Collection<Integer> getChilds(int id) throws TransactionProcessingException {
		return TagController.getChilds(id);
	}

	public static int getTagId(String name) throws TransactionProcessingException {
		return TagController.getTagId(name);
	}

	public static int createTag(int memberId, Tag newTag, boolean authenticated) throws TransactionProcessingException {
		Collection<Integer> parents = newTag.getParents();
		parents.add(BASE_TAG);
		if(newTag.getImgPath() == null) {
			newTag.setImgPath(TagPersistor.getTag(newTag.getParents().iterator().next()).getImgPath());
		}
		int tagId = TagController.createTag(newTag, memberId, authenticated);
		TagController.setParents(tagId, parents);
		return tagId;
	}

	public static Tag editTag(int memberId, int tagId, Tag tag) throws TransactionProcessingException {
		TagController.editTag(memberId, tagId, tag);
		return TagController.getTag(tagId);
	}

	public static Tag getTag(int tagId) throws TransactionProcessingException {
		return TagController.getTag(tagId);
	}

	public static Collection<Tag> search(String keyword, int begin, int offset) throws TransactionProcessingException {
		return TagController.search(keyword, begin, offset);
	}

	public static Collection<Integer> getAuthenticated(
			Collection<Integer> parents) throws TransactionProcessingException {
		Collection<Integer> authTags = new LinkedList<Integer>();
		for(Integer tag: parents) {
			if(getTag(tag).getAuthenticated()) {
				authTags.add(tag);
			}
		}
		return authTags;
	}

	public static Collection<Tag> autoComplete(String keyword) throws TransactionProcessingException {
		return TagController.autoComplete(keyword);
	}
}
