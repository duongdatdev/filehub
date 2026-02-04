# AI-Powered File Search Chat Feature

## Overview
This feature adds an intelligent AI chat popup to the shared files page that helps users find files through natural language conversations. The system uses Google's Gemini AI API to generate intelligent, contextual responses.

## Frontend Components

### AiChatPopup.vue
- **Location**: `src/components/AiChatPopup.vue`
- **Purpose**: Interactive chat interface with AI for file search
- **Features**:
  - Floating circular chat button
  - Expandable chat interface with smooth animations
  - Real-time conversation with AI
  - File suggestions with clickable actions
  - Follow-up recommendations

### aiChatApi.ts
- **Location**: `src/services/aiChatApi.ts`
- **Purpose**: Frontend service for AI chat functionality
- **Features**:
  - Real backend API integration
  - JWT authentication for secure requests
  - Fallback to local search if backend unavailable
  - Integration with existing file API

## Backend Components

### DTOs
- **AiChatRequest.java**: Request structure for chat messages
- **AiChatResponse.java**: Response structure with file suggestions and conversation data

### Service Layer
- **AiChatService.java**: Core AI chat processing service powered by Gemini AI
  - Intent recognition from user messages
  - File search and scoring algorithms
  - **Gemini API Integration**: Uses Google's Gemini 2.0 Flash model for intelligent response generation
  - Fallback response system when AI is unavailable
  - Context-aware conversation handling

### Controller
- **AiChatController.java**: REST API endpoints for AI chat
  - `POST /api/ai/chat` - Process chat messages
  - `GET /api/ai/health` - Health check endpoint

## Gemini AI Integration

### Configuration
The system uses the same Gemini configuration as the file analysis feature:
- **Model**: `gemini-2.0-flash` (configurable)
- **Temperature**: 0.7 (more creative for conversation)
- **Max Output Tokens**: 800 (optimized for chat responses)
- **API Key**: Shared with file analysis service

### AI Response Generation
The AI generates contextual responses by:
1. **Understanding Context**: Analyzes user intent and search results
2. **Crafting Prompts**: Builds detailed prompts with file context
3. **API Communication**: Calls Gemini API with proper error handling
4. **Response Processing**: Extracts and formats AI responses
5. **Fallback System**: Uses predefined responses if AI unavailable

### Intelligent Features
- **Conversational Tone**: Friendly, helpful responses like talking to a colleague
- **Context Awareness**: Understands file search results and user intent
- **Personalized Responses**: Considers user's files, departments, and projects
- **Error Handling**: Graceful degradation when AI service is unavailable

## Key Features

### Intent Recognition
The AI can understand various search intents:
- **RECENT**: "Show me recent files", "latest uploads"
- **POPULAR**: "Most downloaded files", "popular documents"
- **PERSONAL**: "My files", "files I uploaded"
- **PROJECT**: "Project files", specific project names
- **DEPARTMENT**: "Department files", specific department names
- **GENERAL_SEARCH**: Generic file searches by name or type

### Smart File Scoring
Files are scored based on multiple factors:
- **Relevance**: Keyword matching in filename, title, tags
- **Intent-specific scoring**: Recent files get time-based scores, popular files get download-based scores
- **File type popularity**: Common file types (PDF, DOCX, etc.) get preference
- **Context awareness**: User's department and project membership

### Conversational Experience
- Natural language understanding
- Context-aware responses
- File suggestions with reasons why they were recommended
- Follow-up question suggestions
- Error handling with helpful messages

## Integration

### Frontend Integration
The AI chat popup is integrated into the SharedFilesPage.vue component and can be easily added to other pages by importing the component:

```vue
import AiChatPopup from '@/components/AiChatPopup.vue'
```

### Backend Integration
The AI chat service uses existing file services and security utilities:
- Integrates with `FileService` for file retrieval
- Uses `SecurityUtil` for user authentication
- Leverages existing file filtering and search capabilities

## Usage Example

1. **User**: "Show me recent PDF files"
2. **AI**: "I found 3 recent PDF documents for you:"
   - `project-report.pdf` (uploaded yesterday, 5 downloads)
   - `meeting-notes.pdf` (uploaded 3 days ago, PDF document)
   - `specs.pdf` (uploaded last week, in your project)

3. **Follow-up suggestions**: "Would you like to see popular files in your department?"

## Configuration

### CORS Configuration
The backend controller includes CORS configuration for local development:
```java
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
```

### Authentication
The AI chat endpoints are protected by the existing JWT authentication system.

## Future Enhancements

1. **AI Integration**: Replace mock responses with actual AI service (OpenAI, Google Gemini, etc.)
2. **File Content Search**: Search within file contents using existing analysis services
3. **Advanced Filters**: Date ranges, file size, specific users
4. **Conversation History**: Persist chat conversations
5. **File Recommendations**: Machine learning-based file suggestions
6. **Voice Input**: Voice-to-text for hands-free searching

## Testing

The feature includes comprehensive error handling and can be tested by:
1. Opening the shared files page
2. Clicking the blue AI chat button
3. Asking questions like:
   - "Show me recent files"
   - "Find popular documents"
   - "What files did I upload?"
   - "PDF files in my project"

The system gracefully handles various edge cases and provides helpful error messages when needed.
