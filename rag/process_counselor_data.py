import os
import re
import sys
import traceback
import numpy as np
import pandas as pd
from docx import Document
from sentence_transformers import SentenceTransformer
from tqdm import tqdm
import torch
import json

# Paths are supplied by the operator; never commit private source documents.
import argparse

docx_file = None
output_dir = None

def read_docx(file_path):
    """Read text from a docx file"""
    doc = Document(file_path)
    full_text = []
    
    for para in doc.paragraphs:
        if para.text.strip():  # Only add non-empty paragraphs
            full_text.append(para.text.strip())
    
    return full_text

def clean_text(text_list):
    """Clean the text data"""
    cleaned_texts = []
    
    for text in text_list:
        # Remove extra whitespace
        text = re.sub(r'\s+', ' ', text).strip()
        
        # Skip very short or empty texts
        if len(text) < 5:
            continue
        
        # Skip texts that are likely headers or metadata
        if len(text) < 20 and (text.endswith(':') or text.startswith('第') or text.isupper()):
            continue
            
        cleaned_texts.append(text)
    
    return cleaned_texts

def split_into_chunks(text_list, max_chunk_size=512, overlap=50):
    """Split text into chunks with overlap"""
    chunks = []
    
    # First attempt: split by paragraphs
    for text in text_list:
        if len(text) <= max_chunk_size:
            chunks.append(text)
        else:
            # For longer texts, split by sentences first
            sentences = re.split(r'(?<=[。！？.!?])', text)
            current_chunk = ""
            
            for sentence in sentences:
                if not sentence.strip():
                    continue
                
                if len(current_chunk) + len(sentence) <= max_chunk_size:
                    current_chunk += sentence
                else:
                    if current_chunk:
                        chunks.append(current_chunk.strip())
                    
                    # If sentence is too long, further split it
                    if len(sentence) > max_chunk_size:
                        for i in range(0, len(sentence), max_chunk_size - overlap):
                            chunks.append(sentence[i:i + max_chunk_size].strip())
                    else:
                        current_chunk = sentence
            
            if current_chunk:
                chunks.append(current_chunk.strip())
    
    # Remove duplicate chunks and very short chunks
    chunks = [chunk for chunk in chunks if len(chunk) >= 50]
    
    return chunks

def create_embeddings(chunks, model_name="shibing624/text2vec-base-chinese"):
    """Create embeddings using Sentence-BERT"""
    print(f"Loading model: {model_name}")
    model = SentenceTransformer(model_name)
    
    print(f"Creating embeddings for {len(chunks)} chunks...")
    embeddings = model.encode(chunks, show_progress_bar=True, 
                             convert_to_numpy=True)
    
    return embeddings

def save_data(chunks, embeddings, output_dir):
    """Save processed data and embeddings"""
    # Save as CSV with text and embeddings
    df = pd.DataFrame({'text': chunks})
    df.to_csv(os.path.join(output_dir, 'chunks.csv'), index=False, encoding='utf-8')
    
    # Save embeddings as numpy array
    np.save(os.path.join(output_dir, 'embeddings.npy'), embeddings)
    
    # Save as JSON with metadata for each chunk
    data = []
    for i, (chunk, embedding) in enumerate(zip(chunks, embeddings)):
        data.append({
            'id': i,
            'text': chunk,
            'embedding': embedding.tolist()
        })
    
    with open(os.path.join(output_dir, 'data.json'), 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"Saved {len(chunks)} chunks and their embeddings to {output_dir}")

def main():
    global docx_file, output_dir
    parser = argparse.ArgumentParser(description="Build chunks and embeddings from an authorized DOCX knowledge source")
    parser.add_argument("--input", required=True, help="Authorized local .docx file")
    parser.add_argument("--output", required=True, help="Local output directory (contains source text)")
    args = parser.parse_args()
    docx_file, output_dir = args.input, args.output
    os.makedirs(output_dir, exist_ok=True)
    try:
        print(f"Reading document: {docx_file}")
        sys.stdout.flush()
        
        if not os.path.exists(docx_file):
            print(f"ERROR: Document not found at path: {docx_file}")
            return
            
        text_list = read_docx(docx_file)
        print(f"Read {len(text_list)} paragraphs")
        sys.stdout.flush()
        
        # Write a sample of paragraphs for inspection
        with open(os.path.join(output_dir, 'sample_paragraphs.txt'), 'w', encoding='utf-8') as f:
            for i, text in enumerate(text_list[:20]):
                f.write(f"Paragraph {i+1}: {text}\n\n")
        
        print("Cleaning text...")
        sys.stdout.flush()
        cleaned_texts = clean_text(text_list)
        print(f"Cleaned down to {len(cleaned_texts)} paragraphs")
        sys.stdout.flush()
        
        print("Splitting into chunks...")
        sys.stdout.flush()
        chunks = split_into_chunks(cleaned_texts)
        print(f"Created {len(chunks)} chunks")
        sys.stdout.flush()
        
        # Save original chunks before embedding
        print(f"Saving chunks to {output_dir}")
        sys.stdout.flush()
        with open(os.path.join(output_dir, 'original_chunks.txt'), 'w', encoding='utf-8') as f:
            for i, chunk in enumerate(chunks):
                f.write(f"Chunk {i+1}:\n{chunk}\n\n---\n\n")
        
        # Create embeddings
        print("Creating embeddings...")
        sys.stdout.flush()
        embeddings = create_embeddings(chunks)
        
        # Save data
        print("Saving data...")
        sys.stdout.flush()
        save_data(chunks, embeddings, output_dir)
        
        print("Processing complete!")
        sys.stdout.flush()
        
    except Exception as e:
        print(f"ERROR: An exception occurred: {str(e)}")
        traceback.print_exc()
        sys.stdout.flush()

if __name__ == "__main__":
    print("Starting script...")
    sys.stdout.flush()
    main()
    print("Script execution finished.")
    sys.stdout.flush()
