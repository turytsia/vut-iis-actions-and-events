/**
 * This file contains class that implements category services.
 *
 * @author Aleksandr Shevchenko (xshevc01)
 */

package com.project.actionsandevents.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.actionsandevents.Category.exceptions.CategoryNotFoundException;
import com.project.actionsandevents.Category.exceptions.DuplicateCategoryException;
import com.project.actionsandevents.Category.exceptions.CategoryParentException;

import com.project.actionsandevents.Category.requests.CategoryPatchRequest;
import com.project.actionsandevents.Category.requests.CategoryPostRequest;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository repository;

    /**
     * TODO
     * @param id
     * @return
     * @throws CategoryNotFoundException
     */
    public Category getCategoryById(Long id) throws CategoryNotFoundException {
        Optional<Category> category = repository.findById(id);

        if (!category.isPresent()) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }

        return category.get();
    }

    /**
     * TODO
     * @return
     */
    public List<Long> getCategoryIds() {
        return repository.findAllIds();
    }

    /**
     * TODO
     * @param id
     * @param patchRequest
     * @throws CategoryNotFoundException
     */
    public void patchCategoryById(Long id, CategoryPatchRequest patchRequest) 
        throws CategoryNotFoundException, DuplicateCategoryException, CategoryParentException
    {
        Optional<Category> category = repository.findById(id);

        if (!category.isPresent()) {
            throw new CategoryNotFoundException("Category not found with ID: " + id);
        }

        Category categoryToPatch = category.get();

        
        if (patchRequest.getParentCategoryId() != null) {
            Long parentId = patchRequest.getParentCategoryId();
            Optional<Category> parent = repository.findById(parentId);

            if (!parent.isPresent()) {
                throw new CategoryNotFoundException("Parent category not found with ID: " + parentId);
            }

            System.out.println("*****This category: " + id);
            System.out.println("*****Parent category: " + patchRequest.getParentCategoryId());

            // Check if parent category is not the same as the category to patch
            if (parent.get().getId() == categoryToPatch.getId()) {
                throw new CategoryParentException("Category cannot be its own parent");
            }

            // Check if this category is not parent of the parent category
            if (parent.get().getParentCategory() != null) {
                if (parent.get().getParentCategory().getId() == categoryToPatch.getId()) {
                    throw new CategoryParentException("Category cannot be a child of its own child");
                }
            }

            // Check if parent category is not a child of the category to patch
            if (parent.get().getParentCategory() != null) {
                for (Category child : categoryToPatch.getChildCategories()) {
                    if (child.getId() == parent.get().getParentCategory().getId()) {
                        throw new CategoryParentException("Category cannot be a child of its own child");
                    }
                }
            }


            categoryToPatch.setParentCategory(parent.get());
        }

        if (patchRequest.getName() != null) {
            categoryToPatch.setName(patchRequest.getName());
        }

        if (patchRequest.getStatus() != null) {
            categoryToPatch.setStatus(patchRequest.getStatus());
        }

        try {
            repository.save(categoryToPatch);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryException("Category with such parameters already exists");
        }
    }

    /**
     * TODO
     * @param category
     * @return
     * @throws CategoryNotFoundException
     */
    public Long addCategory(CategoryPostRequest category) 
        throws CategoryNotFoundException, DuplicateCategoryException 
    {
        Category newCategory = new Category();

        if (category.getParentCategory() != null) {
            Optional<Category> parent = repository.findById(category.getParentCategory());

            if (!parent.isPresent()) {
                throw new CategoryNotFoundException("Category not found with ID: " + category.getParentCategory());
            }

            System.out.println("*****This category: " + newCategory.getId());
            System.out.println("*****Parent category: " + parent.get().getId());

            // Check if parent category is not the same as the category to patch
            if (parent.get().getId() == newCategory.getId()) {
                throw new CategoryParentException("Category cannot be its own parent");
            }

            // Check if parent category is not a child of the category to patch
            if (parent.get().getParentCategory() != null) {
                if (parent.get().getParentCategory().getId() == newCategory.getId()) {
                    throw new CategoryParentException("Category cannot be a child of its own child");
                }
            }

            newCategory.setParentCategory(parent.get());
        }

        newCategory.setName(category.getName());
        newCategory.setStatus(category.getStatus());

        try {
            return repository.save(newCategory).getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryException("Category with such parameters already exists");
        }
    }

    /**
     * TODO
     * @param category
     * @param id
     * @return
     * @throws CategoryNotFoundException
     */
    public Long addCategoryWithParent(Category category, Long id) 
        throws CategoryNotFoundException, DuplicateCategoryException 
    {
        Optional<Category> parent = repository.findById(id);

        if (!parent.isPresent()) {
            throw new CategoryNotFoundException("Category not found with ID: " + id);
        }

        // Check if parent category is not the same as the category to patch
        if (parent.get().getId() == category.getId()) {
            throw new CategoryParentException("Category cannot be its own parent");
        }

        // Check if parent category is not a child of the category to patch
        if (parent.get().getParentCategory() != null) {
            if (parent.get().getParentCategory().getId() == category.getId()) {
                throw new CategoryParentException("Category cannot be a child of its own child");
            }
        }
        
        category.setParentCategory(parent.get());

        try {
            return repository.save(category).getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryException("Category with such parameters already exists");
        }
    }

    /**
     * TODO
     * @param id
     * @throws CategoryNotFoundException
     */
    public void deleteCategoryById(Long id) throws CategoryNotFoundException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new CategoryNotFoundException("Category with ID " + id + " not found");
        }
    }

    /**
     * TODO
     * @param categoryId
     * @return
     * @throws CategoryNotFoundException
     */
    public String approveCategory(Long categoryId) throws CategoryNotFoundException {
        Optional<Category> category = repository.findById(categoryId);

        if (!category.isPresent()) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }

        category.get().setStatus(CategoryStatus.ACCEPTED);

        return "Category was successfully approved";
    }
}
