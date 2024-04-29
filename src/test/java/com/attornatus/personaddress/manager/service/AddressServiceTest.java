package com.attornatus.personaddress.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.attornatus.personaddress.manager.dto.AddressRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.entity.State;
import com.attornatus.personaddress.manager.repository.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private PersonService personService;
	
	@Mock
	private CityService cityService;
	
	@InjectMocks
	private AddressService addressService;

    @Test
    public void shouldntGetAddressById() {
        Long addressId = 1L;
        
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.getAddressById(addressId));
    }
    
    @Test
    public void shouldGetAddressById() throws NotFoundException {
    	Long addressId = 1L;

    	Address expectedAddress = new Address(addressId, new Person(), new City(),
    			"Colorado Avenue", "899975", "068", true);
    	
    	when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));
    	
    	Address address = addressService.getAddressById(addressId);
    	
    	assertEquals(expectedAddress.getId(), address.getId());
    	assertEquals(expectedAddress.getLocation(), address.getLocation());
    	assertEquals(expectedAddress.getCep(), address.getCep());
    	assertEquals(expectedAddress.getNumber(), address.getNumber());
    	assertEquals(expectedAddress.isMain(), address.isMain());
    }

    @Test
    public void shouldntGetMainAddressByPersonId() {
        Long personId = 1L;
        
        when(addressRepository.getMainAddressByPersonId(personId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.getMainAddressByPersonId(personId));
    }
    
    @Test
    public void shouldGetMainAddressByPersonId() throws NotFoundException {
    	Long personId = 1L;
    	
    	Address expectedAddress = new Address(1L, new Person(personId), new City(),
    			"Colorado Avenue", "899975", "068", true);
    	
    	when(addressRepository.getMainAddressByPersonId(personId)).thenReturn(Optional.of(expectedAddress));
    	
    	Address address = addressService.getMainAddressByPersonId(personId);
    	
    	assertEquals(expectedAddress.getId(), address.getId());
    	assertEquals(expectedAddress.getPerson().getId(), address.getPerson().getId());
    	assertEquals(expectedAddress.getLocation(), address.getLocation());
    	assertEquals(expectedAddress.getCep(), address.getCep());
    	assertEquals(expectedAddress.getNumber(), address.getNumber());
    	assertEquals(expectedAddress.isMain(), address.isMain());
    }

    @Test
    public void shouldntCreateAddress() throws NotFoundException {
    	Long personId = 1L;

    	when(personService.getPersonById(personId))
    	.thenThrow(new NotFoundException("Person with id: " + personId + " not found!"));
    	
    	AddressRequest req = new AddressRequest("Location", "12345", "1A", true, 1L);
    
    	assertThrows(NotFoundException.class, () -> addressService.createAddress(personId, req));
    }
    
    @Test
    public void shouldCreateAddress() throws NotFoundException {
        Long personId = 1L;
        Long cityId = 1L;
        
        AddressRequest req = new AddressRequest("Gamescon Avenue", "25675", "898", true, cityId);

        Person person = new Person(personId, "John Snow", LocalDate.of(2024, 1, 12));
        City city = new City(cityId, "Pimenta Bueno", "RO", new State());
        
    	Address expectedAddress = new Address(1L, person, city,
    			req.location(), req.cep(), req.number(), req.main());
        
        when(personService.getPersonById(personId)).thenReturn(person);
        
        when(cityService.getCityById(cityId)).thenReturn(city);

        when(addressRepository.getMainAddressByPersonId(personId))
        .thenReturn(Optional.empty());
        
        when(addressRepository.save(any())).thenReturn(expectedAddress);

        Address address = addressService.createAddress(personId, req);

        assertNotNull(address);
        assertEquals(req.location(), address.getLocation());
        assertEquals(req.cep(), address.getCep());
        assertEquals(req.number(), address.getNumber());
        assertEquals(req.main(), address.isMain());
        assertEquals(person.getId(), address.getPerson().getId());
        assertEquals(person.getFullName(), address.getPerson().getFullName());
        assertEquals(person.getBirthDate(), address.getPerson().getBirthDate());
        assertEquals(city.getId(), address.getCity().getId());
        assertEquals(city.getName(), address.getCity().getName());
        assertEquals(city.getAcronym(), address.getCity().getAcronym());
    }
    
    @Test
    public void shouldFindAddressesByPersonId() {
    	Long personId = 1L;
    	
    	List<Address> expectedAddressList = List.of(
    			new Address(new Person(personId), "Colorado Avenue", "899975", "068", false),
    			new Address(new Person(personId), "Richardson Avenue", "82385", "128", false),
    			new Address(new Person(personId), "Pacpi Avenue", "25675", "898", true)
    			);
    	
    	when(addressRepository.findAddressesByPersonId(personId)).thenReturn(expectedAddressList);
    	
    	List<Address> addressList = addressService.findAddressesByPersonId(personId);
    	
    	assertEquals(expectedAddressList.size(), addressList.size());
    }
    
    @Test
    public void shouldntSetMainAddress() throws NotFoundException {
    	Long addressId = 1L;
    	
    	when(addressRepository.findById(addressId)).thenReturn(Optional.empty());
    	
    	assertThrows(NotFoundException.class, () -> addressService.setMainAddress(addressId));
    }
    
    @Test
    public void shouldSetMainAddress() throws NotFoundException {
        Long addressId = 1L;
        
        Address expectedAddress = new Address(addressId, new Person(1L), null,
        		"Pacpi Avenue", "25675", "898", false);
        
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));

        Address address = addressService.setMainAddress(addressId);

        assertNotNull(address);
        assertTrue(address.isMain());
    }
    
    @Test
    public void shouldntUpdateAddress() throws NotFoundException {
    	Long addressId = 1L;
    	
    	when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

    	AddressRequest req = new AddressRequest("Colorado Avenue", "899975", "068", true, 1L);
    	
    	assertThrows(NotFoundException.class, () -> addressService.updateAddress(addressId, req));
    }

    @Test
    public void shouldUpdateAddress() throws NotFoundException {
    	Long cityId = 1L;
    	Long addressId = 1L;
    	
    	City city = new City(cityId);
    	
    	Address initialAddress = new Address(addressId, new Person(1L), city,
    			"Pacpi Avenue", "25675", "898", true);
    	
    	AddressRequest req = new AddressRequest("Colorado Avenue", "899975", "068", true, cityId);

    	Address expectedAddress = new Address(addressId, new Person(1L), city,
    			req.location(), req.cep(), req.number(), req.main());
    	
    	when(addressRepository.findById(addressId)).thenReturn(Optional.of(initialAddress));
    	when(cityService.getCityById(cityId)).thenReturn(city);
    	when(addressRepository.save(any())).thenReturn(expectedAddress);

    	Address address = addressService.updateAddress(addressId, req);
    	
    	assertNotNull(address);
    	assertEquals(req.location(), address.getLocation());
    	assertEquals(req.cep(), address.getCep());
    	assertEquals(req.number(), address.getNumber());
    	assertEquals(req.main(), address.isMain());
    	assertEquals(req.cityId(), address.getCity().getId());
    }
    
    @Test
    public void shouldDisableOldMainAddress() throws NotFoundException {
        Long personId = 1L;
        
        Address mockAddress = new Address();
        when(addressRepository.getMainAddressByPersonId(personId)).thenReturn(Optional.of(mockAddress));

        addressService.disableOldMainAddress(personId);

        assertFalse(mockAddress.isMain());
    }
}
