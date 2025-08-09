# SRT - Secure Revocable Web Authentication Protocol  

### Please access the deepwiki page to get the careful and useful help. https://deepwiki.com/techarts0/srt
 
## Project Introduction  
  
SRT is a Java-based secure authentication token system that provides revocable web authentication capabilities. The system implements a sophisticated token management architecture with multiple validation modes and storage backends, designed for enterprise applications requiring fine-grained access control and token revocation capabilities.  
  
Key features include:  
- **Secure Token Generation**: Cryptographically secure tokens with configurable validation modes  
- **Multiple Implementation Modes**: UCM (stateless), PSS (full state), and GWM (whitelist) modes  
- **Flexible Storage Backends**: Support for both MySQL and Redis persistence   
- **Comprehensive Validation**: IP address, user agent, and session-based validation   
  
## Architecture  
  
### Core Components  
  
The SRT architecture follows a layered design with clear separation of concerns:  
  
```mermaid  
graph TB  
    subgraph "Core Interfaces"  
        SRTokenizer["SRTokenizer Interface"]  
    end  
      
    subgraph "Implementation Layer"  
        AbstractSRTokenizer["AbstractSRTokenizer"]  
        UCM["UcmSRTokenizer"]  
        PSS["PssMysqlBasedTokenizer"]  
        GWM["GwmMysqlBasedTokenizer"]  
    end  
      
    subgraph "Core Components"  
        SRToken["SRToken"]  
        Configuration["Configuration"]  
        Session["Session"]  
        MicroState["MicroState"]  
    end  
      
    SRTokenizer --> AbstractSRTokenizer  
    AbstractSRTokenizer --> UCM  
    AbstractSRTokenizer --> PSS  
    AbstractSRTokenizer --> GWM
