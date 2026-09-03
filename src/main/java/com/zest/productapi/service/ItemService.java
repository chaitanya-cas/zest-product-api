package com.zest.productapi.service;

import com.zest.productapi.dto.ItemRequest;
import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.entity.Item;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ItemRepository;
import com.zest.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ProductRepository productRepository;

	@Transactional
	public ItemResponse addItem(Long productId, ItemRequest request) {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

		Item item = Item.builder().product(product).quantity(request.quantity()).build();

		Item savedItem = itemRepository.save(item);

		return new ItemResponse(savedItem.getId(), savedItem.getQuantity());
	}

	@Transactional(readOnly = true)
	public List<ItemResponse> getByProductId(Long productId) {

		if (!productRepository.existsById(productId)) {
			throw new IllegalArgumentException("Product not found with id: " + productId);
		}

		return itemRepository.findByProductId(productId).stream()
				.map(item -> new ItemResponse(item.getId(), item.getQuantity())).toList();
	}
}