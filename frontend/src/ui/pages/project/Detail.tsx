import React from 'react';
import { Project } from 'data/entity';
import { useParams } from 'react-router-dom';

export function ProjectDetailsPage() {
  const params = useParams<{ projectId?: Project['id'] }>();
  const { projectId } = params;

  if (!projectId) {
    return <p>Project ID muss angegeben werden.</p>;
  }
}
