package dev.hornetshell.autobinder;

import com.sun.tools.javac.util.List;
import dev.hornetshell.autobinder.testdto.BigDto;
import dev.hornetshell.autobinder.testdto.SmallDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoMapperTest {


    @Test
    public void testAutoBindSimple() {
        // create a new "Big Dto"
        final BigDto bigDto = new BigDto(UUID.randomUUID(), "test", 5, "lorem ipsum", List.of("alice", "bob"));
        // create an AutoType of SmallDto
        final AutoType<SmallDto> smallDtoAutoType = AutoType.of(SmallDto.class);
        final SmallDto smallDto = AutoMapper.getAutoMapper().cast(bigDto, smallDtoAutoType);

        assertEquals(bigDto.getId(), smallDto.getId());
        assertEquals(bigDto.getName(), smallDto.getName());
        assertEquals(bigDto.getDescription(), smallDto.getDescription());
    }
}
