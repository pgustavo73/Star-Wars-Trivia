package com.example.jettrivia.component

import androidx.compose.ui.graphics.PathEffect
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettrivia.R
import com.example.jettrivia.data.StoreIndex
import com.example.jettrivia.model.QuestionItem
import com.example.jettrivia.screens.QuestionsViewModel
import com.example.jettrivia.util.AppColors
import com.example.jettrivia.util.AppColors.gradient
import com.example.jettrivia.util.AppColors.gradient2
import kotlinx.coroutines.launch

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreIndex = StoreIndex(context)
    val restoreIndex = dataStoreIndex.getIndex.collectAsState(initial = "")
    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember { mutableStateOf(0) }

    if (restoreIndex.value != "") {questionIndex.value = restoreIndex.value.toInt()}
    if (viewModel.data.value.loading == true) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.height(50.dp)
                .width(50.dp),
                color = AppColors.LightYellow
            )
        }
    } else {
        val question = try { questions?.get(questionIndex.value) } catch (ex: Exception){null}
       if (questions != null) {
           QuestionsDisplay(question = question!!, questionIndex = questionIndex,
               viewModel = viewModel){
               questionIndex.value = questionIndex.value +1
               scope.launch {
                   dataStoreIndex.saveIndex(questionIndex.value.toString())
               }
           }
       }
    }
}

@Composable
fun QuestionsDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked: (Int) -> Unit
) {
    val choicesState = remember(question) { question.choices.toMutableList() }
    val answerState = remember(question) { mutableStateOf<Int?>(null) }
    val correctAnswerState = remember(question) { mutableStateOf<Boolean?>(null)}
    val updateAnswer:(Int) -> Unit = remember(question){{
        answerState.value = it
        correctAnswerState.value = choicesState[it] == question.answer
    }}
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    var answerEnable by remember { mutableStateOf( true ) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreAnswer = StoreIndex(context)
    val dataStoreIndex = StoreIndex(context)
    val restoreAnswer = dataStoreAnswer.getAnswer.collectAsState(initial = "")
    var correctAnswer by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient2)
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (restoreAnswer.value != "") {correctAnswer = restoreAnswer.value.toInt()}
            if (questionIndex.value >= 5 ) ShowProgress(score = questionIndex.value, correctAnswer)
            else Title()
            QuestionTracker( counter = questionIndex.value, viewModel.getTotalQuestionsCount())
            CustomLine(pathEffect)

            Column {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 17.sp,
                    color = AppColors.White,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                //choices
                choicesState.forEachIndexed { index, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .border(
                                width = 4.dp, brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.White, AppColors.LightYellow, AppColors.DarkYellow
                                    )
                                ), shape = RoundedCornerShape(15.dp)
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50,
                                    bottomEndPercent = 50,
                                    bottomStartPercent = 50
                                )
                            )
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (answerState.value == index),
                            enabled = answerEnable,
                            onClick = {
                            updateAnswer(index)
                            if (correctAnswerState.value == true && index == answerState.value) {
                                correctAnswer += 1
                                scope.launch {
                                    dataStoreAnswer.saveAnswer(correctAnswer.toString())
                                }
                            }
                            answerEnable = false
                        },
                        modifier = Modifier.padding(start = 16.dp),
                        colors = RadioButtonDefaults.colors( selectedColor =
                        if (correctAnswerState.value == true && index == answerState.value){
                            Color.Green.copy(alpha = 0.3f)
                        } else {Color.Red.copy(alpha = 0.3f)},
                        disabledSelectedColor = if (correctAnswerState.value == true && index == answerState.value){
                            Color.Green.copy(alpha = 0.3f)
                        } else {Color.Red.copy(alpha = 0.3f)}
                        ))

                        val annotatedString = buildAnnotatedString{
                            withStyle( style = SpanStyle(fontWeight = FontWeight.Light,
                                color = if (correctAnswerState.value == true && index == answerState.value){
                                    Color.Green } else if (correctAnswerState.value == false
                                    && index == answerState.value) { Color.Red }
                                else {AppColors.White}, fontSize = 17.sp)) {
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    }
                }
                Button(onClick = { onNextClicked(questionIndex.value)
                    answerEnable = true },
                modifier = Modifier
                    .padding(10.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = buttonColors(
                        containerColor = AppColors.Yellow)) {
                    Text(text = stringResource(R.string.next), modifier = Modifier.padding(4.dp),
                    color = AppColors.Black,
                    fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.height(55.dp))
                FloatingActionButton(modifier = Modifier.align(Alignment.End),
                    onClick = {
                        scope.launch {
                            dataStoreAnswer.saveAnswer("")
                            dataStoreIndex.saveIndex("")
                            questionIndex.value = 0
                            correctAnswer = 0}
                    },
                    containerColor = AppColors.Yellow,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.restart_trivia),
                        tint = AppColors.Black,
                    )
                }
            }
        }
    }
}

@Composable
fun CustomLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = AppColors.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, y = 0f),
            pathEffect = pathEffect
        )
    }
}

//@Preview
@Composable
fun QuestionTracker(
    counter: Int = 10,
    outOf: Int = 50
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    )
                ) {
                    append("Question $counter/")
                    withStyle(
                        style = SpanStyle(
                            color = AppColors.LightGray,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                    ) {
                        append("$outOf")
                    }
                }
            }
        },
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun Title(){
    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .background(Color.Transparent),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.star_wars_trivia),
            style = MaterialTheme.typography.headlineLarge,
            color = AppColors.Yellow,
        )
    }
}

@Preview
@Composable
fun ShowProgress(score: Int = 1, correctAnswer: Int = 0){

    val progressFactor by remember(score){ mutableStateOf(score*0.02f) }

    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.LightGray, AppColors.LightGray
                )
            ),
            shape = RoundedCornerShape(35.dp)
        )
        .clip(
            RoundedCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomEndPercent = 50,
                bottomStartPercent = 50
            )
        )
        .background(Color.Transparent),
    verticalAlignment = Alignment.CenterVertically){
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {},
        modifier = Modifier
            .fillMaxWidth(progressFactor)
            .background(brush = gradient),
        enabled = false,
        elevation = null,
        colors = buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ) ) {
            Text(text = "$correctAnswer Correct",
            modifier = Modifier
                .clip(shape = RoundedCornerShape(23.dp))
                .fillMaxHeight(0.87f)
                .fillMaxWidth()
                .padding(6.dp),
            color = AppColors.White,
            textAlign = TextAlign.Center)

        }
    }
}