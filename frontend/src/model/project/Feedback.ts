import { FeedbackStatus, ProjectRepository, RateResultArgs } from 'data/repositories';
import { Operation } from 'data/Resource';
import { TopicRelevanceChangeEvent } from 'services/event';
import { EventBus } from 'services/event/bus';
import { Container } from 'unstated-typescript';

export type FeedbackResultState = {
  status: Operation;
};

export class FeedbackResultContainer extends Container<FeedbackResultState> {
  public constructor(private repository: ProjectRepository, private eventBus: EventBus) {
    super();
  }

  public async rate(args: RateResultArgs): Promise<void> {
    const stream = Operation.execute(() => this.repository.rateResult(args));
    for await (const event of stream) {
      this.setState({
        ...this.state,
        status: event,
      });
    }
    if (this.state.status.isFinished) {
      this.eventBus.publish(new TopicRelevanceChangeEvent(args));
    }
  }
}
