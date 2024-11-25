package com.SE11.ReceiptOCR.MonthlySubscription;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class MonthlySubscriptionController {

    private final MonthlySubscriptionRepository subscriptionRepository;

    public MonthlySubscriptionController(MonthlySubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // 1. 구독 추가 (DTO 반환)
    @PostMapping
    public ResponseEntity<MonthlySubscriptionDTO> create(@RequestBody MonthlySubscriptionDTO monthlySubscriptionDTO) {
        // DTO -> 엔티티 변환
        MonthlySubscription subscription = new MonthlySubscription();
        subscription.setSubscription_item(monthlySubscriptionDTO.getSubscription_item());
        subscription.setPrice(monthlySubscriptionDTO.getPrice());
        subscription.setCategory(monthlySubscriptionDTO.getCategory());
        subscription.setBilling_date(monthlySubscriptionDTO.getBilling_date());

        // 엔티티 저장
        MonthlySubscription createdSubscription = subscriptionRepository.save(subscription);

        // 저장된 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(createdSubscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 2. 특정 유저의 구독 목록 조회 (DTO 리스트 반환)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MonthlySubscriptionDTO>> getSubscriptionsByUser(@PathVariable String userId) {
        List<MonthlySubscription> subscriptions = subscriptionRepository.findByMemberUserId(userId);

        // 엔티티 리스트 -> DTO 리스트 변환
        List<MonthlySubscriptionDTO> subscriptionDTOs = subscriptions.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(subscriptionDTOs);
    }

    // 3. 특정 구독 ID로 조회 (DTO 반환)
    @GetMapping("/{id}")
    public ResponseEntity<MonthlySubscriptionDTO> getSubscriptionById(@PathVariable int id) {
        MonthlySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        // 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(subscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 4. 구독 정보 수정 (DTO 반환)
    @PutMapping("/{id}")
    public ResponseEntity<MonthlySubscriptionDTO> updateSubscription(
            @PathVariable int id, @RequestBody MonthlySubscriptionDTO monthlySubscriptionDTO) {
        MonthlySubscription existingSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        // 수정된 값 설정
        existingSubscription.setSubscription_item(monthlySubscriptionDTO.getSubscription_item());
        existingSubscription.setPrice(monthlySubscriptionDTO.getPrice());
        existingSubscription.setCategory(monthlySubscriptionDTO.getCategory());
        existingSubscription.setBilling_date(monthlySubscriptionDTO.getBilling_date());

        MonthlySubscription updatedSubscription = subscriptionRepository.save(existingSubscription);

        // 엔티티 -> DTO 변환
        MonthlySubscriptionDTO subscriptionDTO = convertToDTO(updatedSubscription);

        return ResponseEntity.ok(subscriptionDTO);
    }

    // 5. 구독 삭제 (응답 없음)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable int id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new RuntimeException("Subscription not found with id: " + id);
        }
    }

    // 엔티티를 DTO로 변환하는 메서드
    private MonthlySubscriptionDTO convertToDTO(MonthlySubscription subscription) {
        return new MonthlySubscriptionDTO(
                subscription.getSubscription_id(),
                subscription.getSubscription_item(),
                subscription.getPrice(),
                subscription.getCategory(),
                subscription.getBilling_date()
        );
    }
}