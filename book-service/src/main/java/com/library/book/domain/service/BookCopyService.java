package com.library.book.domain.service;

import com.library.book.presentation.dto.request.BookCopyRequestDTO;
import com.library.book.presentation.dto.request.BookCopyUpdateDTO;
import com.library.book.presentation.dto.response.BookCopyResponseDTO;
import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;

import java.util.List;

public interface BookCopyService {
    
    /**
     * Get all copies of a specific book
     * 
     * @param bookId the ID of the book
     * @return list of book copies
     */
    List<BookCopyResponseDTO> getBookCopiesByBookId(Long bookId);
    
    /**
     * Add a new copy to a book
     *
     * @param bookCopyRequestDTO the book copy data
     * @return the created book copy
     */
    BookCopyResponseDTO addBookCopy(BookCopyRequestDTO bookCopyRequestDTO);
    
    /**
     * Update a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @param bookCopyRequestDTO the updated book copy data
     * @return the updated book copy
     */
    BookCopyResponseDTO updateBookCopy(Long bookCopyId, BookCopyUpdateDTO bookCopyRequestDTO);
    
    /**
     * Update the status of a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @param status the new status
     * @return the updated book copy
     */
    BookCopyResponseDTO updateBookCopyStatus(Long bookCopyId, String status);
    
    /**
     * Delete a book copy
     * 
     * @param bookCopyId the ID of the book copy
     * @return true if deleted successfully
     */
    boolean deleteBookCopy(Long bookCopyId);
    
    /**
     * Get a book copy by ID
     * 
     * @param bookCopyId the ID of the book copy
     * @return the book copy
     */
    BookCopyResponseDTO getBookCopyById(Long bookCopyId);
} 