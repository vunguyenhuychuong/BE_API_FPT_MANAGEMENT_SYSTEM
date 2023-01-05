package com.java8.tms.common.repository;

import com.java8.tms.common.dto.TrainingProgramDTO;
import com.java8.tms.common.entity.TrainingProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Test Training program Repository
 * </p>
 *
 * @author Acer
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainingProgramRepositoryTest {

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ModelMapper modelMapper;

    /**
     * <p>
     * Init model mapper. Can not autowired because that is Bean in Config
     * </p>
     *
     * @author Vien Binh
     */
    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
    }

    /**
     * <p>
     * Get training program by uuid
     * </p>
     *
     * @author Vien Binh
     */
    @Test
    void testFindById_thenReturnProgram() {
        UUID uuid = UUID.fromString("2f4b0772-4208-4a74-a3f8-d86b5df0fe4a");
        TrainingProgram trainingProgram = trainingProgramRepository.findById(uuid).get();
        TrainingProgramDTO trainingProgramDTO = modelMapper.map(trainingProgram, TrainingProgramDTO.class);
        assertThat(trainingProgramDTO.getName()).isEqualTo("Fullstack Java Web Developer Foundation");
    }

}
