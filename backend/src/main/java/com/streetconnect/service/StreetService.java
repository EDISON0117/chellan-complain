package com.streetconnect.service;

import com.streetconnect.dto.StreetDto;
import com.streetconnect.entity.Street;
import com.streetconnect.exception.DuplicateResourceException;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.ComplaintRepository;
import com.streetconnect.repository.StreetRepository;
import com.streetconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StreetService {

    private final StreetRepository streetRepository;
    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    @Autowired
    public StreetService(StreetRepository streetRepository,
                         UserRepository userRepository,
                         ComplaintRepository complaintRepository) {
        this.streetRepository = streetRepository;
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
    }

    @Transactional(readOnly = true)
    public List<StreetDto> getAllStreets() {
        return streetRepository.findAllByOrderByCityAscNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StreetDto getStreetById(Long id) {
        Street street = streetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Street not found with id: " + id));
        return convertToDto(street);
    }

    @Transactional(readOnly = true)
    public Street getStreetEntityById(Long id) {
        return streetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Street not found with id: " + id));
    }

    @Transactional
    public StreetDto createStreet(StreetDto dto) {
        if (streetRepository.findByNameAndAreaAndCity(dto.getName(), dto.getArea(), dto.getCity()).isPresent()) {
            throw new DuplicateResourceException("Street already exists in " + dto.getArea() + ", " + dto.getCity());
        }

        Street street = new Street();
        street.setName(dto.getName());
        street.setArea(dto.getArea());
        street.setCity(dto.getCity());
        street.setPincode(dto.getPincode());
        if (dto.getState() != null && !dto.getState().trim().isEmpty()) {
            street.setState(dto.getState());
        }

        Street saved = streetRepository.save(street);
        return convertToDto(saved);
    }

    public StreetDto convertToDto(Street street) {
        if (street == null) return null;

        StreetDto dto = new StreetDto();
        dto.setId(street.getId());
        dto.setName(street.getName());
        dto.setArea(street.getArea());
        dto.setCity(street.getCity());
        dto.setPincode(street.getPincode());
        dto.setState(street.getState());
        dto.setCreatedAt(street.getCreatedAt());

        long residentsCount = userRepository.countByStreetId(street.getId());
        long complaintsCount = complaintRepository.countByStreetId(street.getId());

        dto.setTotalResidents(residentsCount);
        dto.setTotalComplaints(complaintsCount);

        return dto;
    }
}
