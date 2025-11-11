package bham.team.service.mapper;

import bham.team.domain.FindTime;
import bham.team.domain.Suggestion;
import bham.team.service.dto.FindTimeDTO;
import bham.team.service.dto.SuggestionDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Suggestion} and its DTO {@link SuggestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SuggestionMapper extends EntityMapper<SuggestionDTO, Suggestion> {
    @Mapping(target = "findTimes", source = "findTimes", qualifiedByName = "findTimeIdSet")
    SuggestionDTO toDto(Suggestion s);

    @Mapping(target = "removeFindTime", ignore = true)
    Suggestion toEntity(SuggestionDTO suggestionDTO);

    @Named("findTimeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FindTimeDTO toDtoFindTimeId(FindTime findTime);

    @Named("findTimeIdSet")
    default Set<FindTimeDTO> toDtoFindTimeIdSet(Set<FindTime> findTime) {
        return findTime.stream().map(this::toDtoFindTimeId).collect(Collectors.toSet());
    }
}
