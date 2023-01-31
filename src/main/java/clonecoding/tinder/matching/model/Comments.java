package clonecoding.tinder.matching.model;

import clonecoding.tinder.matching.model.dto.CommentRedisResponseDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comments {

    private List<CommentRedisResponseDto> comments = new ArrayList<>();
}
